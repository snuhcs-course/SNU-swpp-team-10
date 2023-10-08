const express = require('express');
const router = express.Router();
const {renderHelloWorld} = require('../controllers')

router.get('/', renderHelloWorld);

module.exports = router;