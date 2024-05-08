var express = require('express');
var router = express.Router();
var user Controller = require('../controllers/user Controller.js');

/*
 * GET
 */
router.get('/', user Controller.list);

/*
 * GET
 */
router.get('/:id', user Controller.show);

/*
 * POST
 */
router.post('/', user Controller.create);

/*
 * PUT
 */
router.put('/:id', user Controller.update);

/*
 * DELETE
 */
router.delete('/:id', user Controller.remove);

module.exports = router;
