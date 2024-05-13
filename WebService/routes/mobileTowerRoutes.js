var express = require('express');
var router = express.Router();
var mobileTowerController = require('../controllers/mobileTowerController.js');

/*
 * GET
 */
router.get('/', mobileTowerController.list);

/*
 * GET
 */
router.get('/:id', mobileTowerController.show);

/*
 * POST
 */
router.post('/', mobileTowerController.create);

/*
 * PUT
 */
router.put('/:id', mobileTowerController.update);

/*
 * DELETE
 */
router.delete('/:id', mobileTowerController.remove);

module.exports = router;
