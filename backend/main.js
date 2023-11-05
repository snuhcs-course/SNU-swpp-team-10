const mysql = require('mysql');
const express = require('express');
const bodyParser = require('body-parser');
const axios= require('axios');
const app = express();
const port = 3000;

app.set("port", port);
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({extended: true}));

const OPENAI_API_URL= 'https://api.openai.com/v1/chat/completions'
const OPENAI_API_KEY='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';


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


app.post('/manager/send', async (req, res) => {
    const userMessage = req.body.message;
    const category_list = req.body.category;
    const todo_list = req.body.todo;
    const schedule_list=req.body.schedule;
    const time = req.body.time;

    console.log(category_list, todo_list, schedule_list, time);
    const systemPrompt = `In sqlite, given two tables:
    schedule(id INTEGER, title TEXT, start_time TEXT, end_time TEXT, memo TEXT DEFAULT NULL, repeat_group_id=INTEGER DEFAULT NULL, category_id DEFAULT NULL, priority INTEGER DEFAULT 3, show_in_monthly_view INTEGER DEFAULT 1)
    todo(id INTEGER, title TEXT, due_time TEXT, memo TEXT DEFAULT NULL, repeat_group_id=INTEGER DEFAULT NULL, category_id DEFAULT NULL, priority INTEGER DEFAULT 3, show_in_monthly_view INTEGER DEFAULT 0, is_overridden DEFAULT 0)

current time is [2023-11-03 12:25:39]. follow the same format. 
when modifying time attributes partially, for example only changing the date, use substr() and concatenation.


Convert user command into sql queries and return single string. make the query as short as possible. do NOT include any other text.

Schedules are events that have both start time & end time. Todo's are tasks that only have due time. If uncertain whether command is referring to todo or schedule, include queries for both.

given data below on title and id's of category, todo, schedule:

for condition on category, search for the id and use in query. 

ONLY when there is no matching category, it is a condition on title. NEVER use WHERE condition on title. Instead, search for the id's from the below data and use id explicitly in query. If there is no match in both schedule and todo, use "NO_SUCH_PLAN;" in place of that query. Even when you find the matching id, NEVER forget to include WHERE conditions on time.

category(id, title): ${category_list}

todo(id, title): ${todo_list}

schedule(id, title): ${schedule_list}`


    try {
        const response = await axios.post(OPENAI_API_URL, {
            model: "gpt-4",
            messages: [
                {
                    role: "system",
                    content: systemPrompt 
                },
                // {
                //     role: "assistant",
                //     content: assistantPrompt 
                // },
                //contains past responses. provide example responses for better accuracy
    
                {
                    role: "user",
                    content: userMessage 
                }
            ],
            // top_p:0.1,
            // max_tokens:500,
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

// app.post('/user/write', (req, res) => {
//     console.log(req.body);
//     const userName = req.body.userName;
//     const userId = req.body.userId;

//     const sql = 'INSERT INTO Users (UserName, UserId) VALUES (?, ?)';
//     const params = [userName, userId];

//     connection.query(sql, params, (err, result) => {
//         let resultCode = 404;
//         let message = 'Error occured';

//         if (err) {
//             console.log(err);
//         } else {
//             resultCode = 200;
//             message = 'Write Success';
//             console.log(message);
//         }

//         res.json({
//             'code': resultCode,
//             'message': message
//         });
//     });
// });

// app.post('/user/read', (req, res) => {
//     console.log(req.body);
//     const userName = req.body.userName;
//     const sql = 'select * from Users where UserName = ?';
//     const params = [userName];

//     connection.query(sql, params, (err, result) => {
//         let resultCode = 404;
//         let message = 'Error occured';
//         let userId = -1;

//         if (err) {
//             console.log(err);
//         } else {
//             if (result.length === 0) {
//                 resultCode = 404;
//                 message = "User '" + userName + "' does not exist";
//                 userId = "0000-00000";
//             } else {
//                 resultCode = 200;
//                 message = 'Read Success';
//                 userId = result[0].UserId;
//             }
//         }
//         res.json({
//             'code': resultCode,
//             'message': message,
//             'userId' : userId,
//         });
//     })
// });