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


const systemPrompt = `Your job is to convert user commands into sql queries in sqlite and return single string. Make the query as short as possible. Do not specify and default values when inserting. Do NOT include any other text.

    In the database, three tables exist:
    SCHEDULE(id INTEGER, title TEXT, start_time TEXT, end_time TEXT, memo TEXT DEFAULT NULL, category_id DEFAULT NULL, priority INTEGER DEFAULT NULL, show_in_monthly_view INTEGER DEFAULT 1)
    
    TODO(id INTEGER, title TEXT, due_time TEXT, memo TEXT DEFAULT NULL, category_id DEFAULT NULL, priority INTEGER DEFAULT NULL, show_in_monthly_view INTEGER DEFAULT 0)
    
    CATEGORY(id INTEGER, title TEXT, priority INTEGER)
    
    SCHEDULE is an event that has both start time & end time. TODO is a task that only has due time. The term PLAN includes both TODO and SCHEDULE. In PLAN tables, category_id is a foreign key referencing CATEGORY.id. priority is from 1~5, higher the more important.
    
    You will be given (id, title) tuples of existing records in each table, and current time.
    
    Follow [YYYY-MM-DD HH:MM:SS] format for time. When modifying time attributes partially, for example only changing the date, use substr() and concatenation. '오전 12시' means 00:00:00.
    
    -------------------------------------------------------------------------
    #Conversion Instructions
    
    First, determine the type of the query as one of the four types given: INSERT/DELETE/UPDATE/SELECT
    NEVER consider the type as UPDATE unless when explicitly commanded to do so such as "바꿔, 수정".  Otherwise, INSERT a new plan.
    
    After the query type has been determined, determine whether the PLAN type is SCHEDULE or TODO. If the determined query type is INSERT, the PLAN type should be TODO by default. Only when the time span is explicitly given by specifying both the start time and end time explicitly one-by-one, it becomes SCHEDULE. For example, specifying only single date means PLAN type is TODO, and due_time is 23:59:59 of that day.  
    
    When the type is INSERT, find the matching category id from the data given and use the id. If you can't find matching CATEGORY.id from data below, leave it as null. DO NOT use WHERE condition on title of CATEGORY.
    
    For a command including conditional clause on category, search for the id of the category with matching title in CATEGORY and use the id in query. 
    ONLY when there is no matching CATEGORY record, it is a condition on title. NEVER use WHERE condition on title of TODO or SCHEDULE. Instead, search for the id of records with matching title and use id explicitly in query. If there is no match in both SCHEDULE and TODO, output "NO_SUCH_PLAN;" in place of that query. Even when you find the matching id, NEVER forget to include WHERE conditions on time.`
    
const assistant_exemplars=[
    {
        "role": "user",
        "content": `Current Time: [2023-11-23 20:25:39]

        CATEGORY(id, title): (1, "과제"), (2, "잡일"), (3, "운동"), (4, "약속"), (5, "시험"), (6, "봉사")
        
        TODO(id, title): (1, "쓰레기 버리기"), (2, "밥먹기"), (3, "키보드 사기"), (4, "닭가슴살 시키기"), 
        (5, "엄마한테 전화하기"), (6, "알고리즘 과제2"), (7, "알고리즘 과제1"), 
        (8, "인공지능 과제"), (9, "소개원실 회의"), (10, "회의"), (11, "컴퓨터 구조 과제")
        
        SCHEDULE(id, title): (1, "신영이랑 밥먹기"), (2, "알고리즘 수업"), (3, "헬스'), (4, "과외"),
        (5, "데이트"), (6, "자전거 타기'), (7, "디자인 워크샵"), (8, "영화감상회"),
        (9, "신영이랑 커피"), (10, "고급 수학 과외")`
    },
    {
        "role": "user",
        "content": "23년 11월 18일 컴퓨터구조 과제 삭제해줘"
    },
    {
        "role": "assistant",
        "content": `"DELETE FROM TODO WHERE id=11 AND date(due_time)='2023-11-18';"`
    },
    {
        "role": "user",
        "content": "내일 과제 모두 삭제"
    },
    {
        "role": "assistant",
        "content": `"DELETE FROM TODO WHERE category_id=1 AND date(due_time)='2023-11-24';”`
    },
    {
        "role": "user",
        "content": "내일 8시 알골 과제 추가"
    },
    {
        "role": "assistant",
        "content": `"INSERT INTO TODO (title, due_time, category_id) VALUES ('알골 과제', '2023-11-24 08:00:00', 1);"`
    },
    {
        "role": "user",
        "content": "이번 달 매주 화요일에 헬스하기 추가해줘"
    },
    {
        "role": "assistant",
        "content": `"INSERT INTO TODO(title, due_time) VALUES('헬스하기', '2023-11-07 23:59:59'), ('헬스하기', '2023-11-14 23:59:59'), ('헬스하기', '2023-11-21 23:59:59'), ('헬스하기', '2023-11-28 23:59:59');”`
    },
    {
        "role": "user",
        "content": "3시간 후에 밥먹기"
    },
    {
        "role": "assistant",
        "content": `"INSERT INTO TODO (title, due_time) VALUES ('밥먹기', datetime('now', '+3 hours'));"`
    },
    {
        "role": "user",
        "content": "키보드 사기를 스케쥴로 바꿔줘."
    },
    {
        "role": "assistant",
        "content": `"INSERT INTO SCHEDULE (title, start_time, end_time, memo, category_id, priority) SELECT title, due_time, due_time, memo, category_id, priority FROM TODO WHERE id=3; DELETE FROM TODO WHERE id=3"`
    },
    {
        "role": "user",
        "content": "신영이랑 밥먹기를 투두로 바꿔줘."
    },
    {
        "role": "assistant",
        "content": `"INSERT INTO TODO (title, due_time, memo, category_id, priority) SELECT title, start_time, memo, category_id, priority FROM SCHEDULE WHERE id=1; DELETE FROM SCHEDULE WHERE id=1;"`
    },
    {
        "role": "user",
        "content": "이번달 과제 모두 삭제해줘."
    },
    {
        "role": "assistant",
        "content": `"DELETE FROM TODO WHERE category_id=1 AND date(due_time) LIKE '2023-11%';"`
    },

]
const messages=[
    {
        role: "system",
        content: systemPrompt 
    }]

messages.push(...assistant_exemplars);

app.post('/manager/send', async (req, res) => {
    const userMessage = req.body.message;
    const category_list = req.body.category;
    const todo_list = req.body.todo;
    const schedule_list=req.body.schedule;
    const time = req.body.time;

    const userData = `Current time: [${time}]
    
    CATEGORY(id, title): ${category_list}

    TODO(id, title): ${todo_list}

    SCHEDULE(id, title): ${schedule_list}
    `
    
    messages.push({
        role: "user",
        content: userData
    },
    {
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


