var express = require('express');
var router = express.Router();
var measurementController = require('../controllers/measurementController.js');
var authenticateToken = require('../middleware/authenticateToken.js');
var adminCheck = require('../middleware/adminCheck.js');


/*
 * Blockchain Routes
 */
router.get('/blockchain', measurementController.getBlockChain);  // Route for retrieving the blockchain in JSON format
router.get('/nextMeasurement', measurementController.getNextMeasurement);  // Route for fetching the next measurement in JSON format
router.get('/confirmMined', measurementController.confirmMined);
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
router.post('/createMany', authenticateToken, adminCheck, measurementController.createMany);
router.post('/blockchain', measurementController.saveBlockChain);

/*
 * PUT
 */
router.put('/:id', authenticateToken, adminCheck, measurementController.update);

/*
 * DELETE
 */
router.delete('/:id', authenticateToken, measurementController.remove);



module.exports = router;

