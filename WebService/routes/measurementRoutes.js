var express = require('express');
var router = express.Router();
var measurementController = require('../controllers/measurementController.js');

/*
 * GET
 */
router.get('/', measurementController.listAll);
router.get('/measure', measurementController.listNearby);
router.get('/measureRectangle', measurementController.listWithinRectangle);

/*
 * GET
 */
router.get('/:id', measurementController.show);

/*
 * POST
 */
router.post('/', measurementController.create);

/*
 * PUT
 */
router.put('/:id', measurementController.update);

/*
 * DELETE
 */
router.delete('/:id', measurementController.remove);

module.exports = router;
