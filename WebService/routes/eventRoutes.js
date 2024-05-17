var express = require('express');
var router = express.Router();
var eventController = require('../controllers/eventController.js');
var authenticateToken = require('../middleware/authenticateToken.js');

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
router.post('/', authenticateToken, eventController.create);

/*
 * PUT
 */
router.put('/:id', authenticateToken, eventController.update);

/*
 * DELETE
 */
router.delete('/:id', authenticateToken, eventController.remove);

module.exports = router;
