const mysql = require('mysql');
const express = require('express');
const bodyParser = require('body-parser');
const axios= require('axios');
const app = express();
const port = 3000;
const config=require('./config.json');

app.set("port", port);
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({extended: true}));

const OPENAI_API_URL= 'https://api.openai.com/v1/chat/completions'
const OPENAI_API_KEY=config.openai_api_key;


app.get('/', (req, res) => {
  res.send('Hello World!');
});

app.listen(port, () => {
    console.log('Server Runnning...');
});

const connection = mysql.createConnection({
    host: "calendy-database.csiusnffabei.ap-northeast-2.rds.amazonaws.com",
    user: "admin",
    database: "calendy_databse",
    password: "jajuboja10",
    port: 3306
});



const assistant_exemplars=[
    {
        "role": "user",
        "content": "내일 컴퓨터구조 과제 삭제해줘"
    },
    {
        "role": "assistant",
        "content": `"DELETE FROM todo WHERE id=133 AND date(due_time)='2023-11-05';"`
    },
    {
        "role": "user",
        "content": "내일 모레 컴구 과제 삭제"
    },
    {
        "role": "assistant",
        "content": `"DELETE FROM todo WHERE id=133 AND date(due_time)='2023-11-06';"`
    },
    {
        "role": "user",
        "content": "남은 11월 매주 화요일에 헬스하기 추가해줘"
    },
    {
        "role": "assistant",
        "content": `"INSERT INTO TODO(title, due_time) VALUES('헬스하기', '2023-11-07 23:59:59'), ('헬스하기', '2023-11-14 23:59:59'), ('헬스하기', '2023-11-21 23:59:59'), ('헬스하기', '2023-11-28 23:59:59');”`
    },
    {
        "role": "user",
        "content": "과제 카테고리 금요일로 옮겨줘"
    },
    {
        "role": "assistant",
        "content": `"UPDATE TODO SET due_time = date(due_time, 'weekday 5') || substr(due_time, 11) WHERE category_id=1;"`
    },
    {
        "role": "user",
        "content": "내일 과제 모두 삭제"
    },
    {
        "role": "assistant",
        "content": `"DELETE FROM TODO WHERE category_id=1 AND date(due_time)='2023-11-04';"`
    }
]

app.post('/manager/send', async (req, res) => {
    const userMessage = req.body.message;
    const category_list = req.body.category;
    const todo_list = req.body.todo;
    const schedule_list=req.body.schedule;
    const time = req.body.time;
    const systemPrompt = `Your job is to convert user commands into sql queries in sqlite and return single string. Make the query as short as possible. Do not specify and default values when inserting. Do NOT include any other text.

    In the database, three tables exist:
    SCHEDULE(id INTEGER, title TEXT, start_time TEXT, end_time TEXT, memo TEXT DEFAULT NULL, repeat_group_id=INTEGER DEFAULT NULL, category_id DEFAULT NULL, priority INTEGER DEFAULT NULL, show_in_monthly_view INTEGER DEFAULT 1)
    TODO(id INTEGER, title TEXT, due_time TEXT, memo TEXT DEFAULT NULL, repeat_group_id=INTEGER DEFAULT NULL, category_id DEFAULT NULL, priority INTEGER DEFAULT NULL, show_in_monthly_view INTEGER DEFAULT 0, is_overridden DEFAULT 0)
    CATEGORY(id INTEGER, title TEXT, priority INTEGER)
    
    SCHEDULE is an event that has both start time & end time. TODO is a task that only has due time. The term PLAN includes both TODO and SCHEDULE. In PLAN tables, category_id is a foreign key referencing CATEGORY.id.  
    
    You will be given (id, title) tuples of existing records in each table.
    
    Current time is ${time}. follow the same format for time. Keep in mind current time is different from the time in past conversations. When modifying time attributes partially, for example only changing the date, use substr() and concatenation. '오전 12시' means 00:00.
    
    -------------------------------------------------------------------------
    #Conversion Instructions
    
    First, determine the type of the query as one of the four types given: INSERT/DELETE/UPDATE/SELECT
    When the command is unclear about inserting or updating, consider the type INSERT. Only consider the type as UPDATE when explicitly commanded to do so.
    
    After the query type has been determined, determine whether the PLAN type is SCHEDULE or TODO. If the determined query type is INSERT, the PLAN type should be TODO by default. Only when the time span is explicitly given by specifying both the start time and end time explicitly one-by-one, it becomes SCHEDULE. For example, specifying only single date means PLAN type is TODO, and due_time is 23:59:59 of that day.  
    
    When the type is INSERT, find the matching category id from the data given and use the id. If you can't find matching CATEGORY.id from data below, leave it as null. DO NOT use WHERE condition on title of CATEGORY.
    
    For a command including conditional clause on category, search for the id of the category with matching title in CATEGORY and use the id in query. 
    ONLY when there is no matching CATEGORY record, it is a condition on title. NEVER use WHERE condition on title of TODO or SCHEDULE. Instead, search for the id of records with matching title and use id explicitly in query. If there is no match in both SCHEDULE and TODO, output "NO_SUCH_PLAN;" in place of that query. Even when you find the matching id, NEVER forget to include WHERE conditions on time.
    
    ------------------------------------------------------------------------------
    #Input Data
    
    CATEGORY(id, title): ${category_list}
    TODO(id, title): ${todo_list} 
    SCHEDULE(id, title): ${schedule_list}`
    const messages=[
        {
            role: "system",
            content: systemPrompt 
        }]

    Array.prototype.push.apply(messages, assistant_exemplars);
    messages.push({
        role: "user",
        content: userMessage 
    })
    


    try {
        const response = await axios.post(OPENAI_API_URL, {
            model: "gpt-4",
            messages: messages,
            temperature: 0
        }, {
            headers: {
                'Authorization': `Bearer ${OPENAI_API_KEY}`,
                'Content-Type': 'application/json'
            }
        });

        const gptResponse = response.data.choices[0].message.content;
        //gptResponse is a string-formatted json with array of string sql queries in attribute "queries"
        res.send(gptResponse); 

    } catch (error) {
        console.error('Error querying OpenAI:', error.response.data);
        res.status(500).json({ error: 'Failed to get a response from OpenAI' });
    }
});


