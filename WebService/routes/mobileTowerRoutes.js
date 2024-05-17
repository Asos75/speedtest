var express = require('express');
var router = express.Router();
var mobileTowerController = require('../controllers/mobileTowerController.js');
var authenticateToken = require('../middleware/authenticateToken.js');

/*
 * GET
 */
router.get('/', mobileTowerController.list);

/*
 * GET
 */
router.get('/:id', mobileTowerController.show);
router.get('/confirmed/:status', mobileTowerController.confirmed)
router.get('/confirm/:id', authenticateToken, mobileTowerController.confirm)
/*
 * POST
 */
router.post('/', authenticateToken, mobileTowerController.create);
router.post('/createMany', authenticateToken, mobileTowerController.createMany)
/*
 * PUT
 */

router.put('/:id', authenticateToken, mobileTowerController.update);

/*
 * DELETE
 */
router.delete('/:id', authenticateToken, mobileTowerController.remove);

module.exports = router;
