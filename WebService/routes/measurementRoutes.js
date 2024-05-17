var express = require('express');
var router = express.Router();
var measurementController = require('../controllers/measurementController.js');
var authenticateToken = require('../middleware/authenticateToken.js');
var adminCheck = require('../middleware/adminCheck.js');

/*
 * GET
 */
router.get('/', measurementController.listAll);
router.get('/measure/:lat/:lon/:radius', measurementController.listNearby);
router.get('/measureRectangle/:lat1/:lon1/:lat2/:lon2', measurementController.listWithinRectangle);

/*
 * GET
 */
router.get('/user/:user_id', measurementController.listByUser);
router.get('/timeframe/:date_start/:date_end', measurementController.listByTimeFrame);
router.get('/:id', measurementController.show);

/*
 * POST
 */
router.post('/', measurementController.create);
router.post('/createMany', authenticateToken, adminCheck, measurementController.createMany)

/*
 * PUT
 */
router.put('/:id', authenticateToken, adminCheck, measurementController.update);

/*
 * DELETE
 */
router.delete('/:id', authenticateToken, adminCheck, measurementController.remove);

module.exports = router;
