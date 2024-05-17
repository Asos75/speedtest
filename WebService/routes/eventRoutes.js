var express = require('express');
var router = express.Router();
var eventController = require('../controllers/eventController.js');
var authenticateToken = require('../middleware/authenticateToken.js');
var adminCheck = require('../middleware/adminCheck.js');

/*
 * GET
 */
router.get('/', eventController.list);

/*
 * GET
 */
router.get('/:id', eventController.show);

/*
 * POST
 */
router.post('/', authenticateToken, adminCheck, eventController.create);

/*
 * PUT
 */
router.put('/:id', authenticateToken, adminCheck, eventController.update);

/*
 * DELETE
 */
router.delete('/:id', authenticateToken, adminCheck, eventController.remove);

module.exports = router;
