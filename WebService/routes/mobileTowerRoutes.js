var express = require('express');
var router = express.Router();
var mobileTowerController = require('../controllers/mobileTowerController.js');
var authenticateToken = require('../middleware/authenticateToken.js');
var adminCheck = require('../middleware/adminCheck.js');

/*
 * GET
 */
router.get('/', mobileTowerController.list);

/*
 * GET
 */
router.get('/:id', mobileTowerController.show);
router.get('/confirmed/:status', mobileTowerController.confirmed)
router.get('/confirm/:id', authenticateToken, adminCheck, mobileTowerController.confirm)
/*
 * POST
 */
router.post('/', authenticateToken, mobileTowerController.create);
router.post('/autoconfirm', mobileTowerController.createAutoconfirm)
router.post('/locate', mobileTowerController.locate)
router.post('/addconfirm', mobileTowerController.addConfirm)
router.post('/createMany', authenticateToken, adminCheck, mobileTowerController.createMany)
/*
 * PUT
 */

router.put('/:id', authenticateToken, adminCheck, mobileTowerController.update);

/*
 * DELETE
 */
router.delete('/:id', authenticateToken, adminCheck, mobileTowerController.remove);

module.exports = router;
