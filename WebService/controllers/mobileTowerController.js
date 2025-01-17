const mobileTowerModel = require('../models/mobileTowerModel.js');
var MobiletowerModel = require('../models/mobileTowerModel.js');

const sharp = require("sharp");
const OpenAI = require("openai");

const openai = new OpenAI({
    apiKey: process.env.AI, // Use your OpenAI API key from environment variables
});

/**
 * mobileTowerController.js
 *
 * @description :: Server-side logic for managing mobileTowers.
 */
module.exports = {

    /**
     * mobileTowerController.list()
     */
    list: function (req, res) {
        MobiletowerModel.find(function (err, mobileTowers) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when getting mobileTower.',
                    error: err
                });
            }

            return res.json(mobileTowers);
        }).populate("locator");
    },

    /**
     * mobileTowerController.show()
     */
    show: function (req, res) {
        var id = req.params.id;

        MobiletowerModel.findOne({_id: id}, function (err, mobileTower) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when getting mobileTower.',
                    error: err
                });
            }

            if (!mobileTower) {
                return res.status(404).json({
                    message: 'No such mobileTower'
                });
            }

            return res.json(mobileTower);
        }).populate("locator");
    },

    confirmed: function (req, res) {
        var status = req.params.status;

        MobiletowerModel.find({confirmed: status}, function(err, mobileTowers){
            if (err) {
                return res.status(500).json({
                    message: 'Error when getting mobileTower.',
                    error: err
                });
            }

            return res.json(mobileTowers);
        }).populate("locator")
    },

    /**
     * mobileTowerController.create()
     */
    create: function (req, res) {
        var mobileTower = new MobiletowerModel({
			location : req.body.location,
			operator : req.body.operator,
			type : req.body.type,
			confirmed : req.body.confirmed,
			locator : req.body.locator
        });

        mobileTower.save(function (err, mobileTower) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when creating mobileTower',
                    error: err
                });
            }

            return res.status(201).json(mobileTower);
        });
    },

    createAutoconfirm: async function (req, res) {
        try {
            if (!req.files || !req.files.image) {
                return res.status(400).json({ message: "No image file uploaded" });
            }
    
            const imageFile = req.files.image;
    
            const axios = require('axios');
            const FormData = require('form-data');
            const formData = new FormData();
            formData.append('image', imageFile.data, imageFile.name);
    
            const flaskApiUrl = process.env.VISION_IP + '/predict_image'; 
            const flaskResponse = await axios.post(flaskApiUrl, formData, {
                headers: formData.getHeaders(),
            });
    
            var confirmed = true;

            if (!flaskResponse.data.is_tower) {
                confirmed = false;
            }
    
            const mobileTower = new MobiletowerModel({
                location: req.body.location,
                operator: req.body.operator,
                type: req.body.type,
                confirmed: confirmed,
                locator: req.body.locator,
            });
    
            const savedTower = await mobileTower.save();
            

            return res.status(201).json({
                message: 'Mobile Tower created and confirmed automatically',
                data: savedTower,
            });
        } catch (error) {
            return res.status(500).json({
                message: 'Error in createAutoconfirm',
                error: error.message,
            });
        }
    },

    locate: async function(req, res) {
        const axios = require('axios');

        try {
            if (!req.files || !req.files.image) {
                return res.status(400).json({ message: "No image file uploaded" });
            }
    
            const imageFile = req.files.image;
    
            const FormData = require('form-data');
            const formData = new FormData();
            formData.append('image', imageFile.data, imageFile.name);
    
            const flaskApiUrl = process.env.VISION_IP + '/predict_uploaded_image';
    
            const flaskResponse = await axios.post(flaskApiUrl, formData, {
                headers: formData.getHeaders(),
            });
    
            const { filename, confidence, x1, y1, x2, y2, image } = flaskResponse.data;
    
            return res.status(200).json({
                message: 'Tower location detected successfully',
                filename,
                confidence,
                location: { x1, y1, x2, y2 },
                image,
            });
        } catch (error) {
            console.error("Error in locate function:", error);
            return res.status(500).json({
                message: 'Error processing image for tower location',
                error: error.message,
            });
        }
    },
    

    createMany: function(req, res) {
        var towers = req.body.towers;

        if (!Array.isArray(towers)) {
            return res.status(400).json({
                message: 'Invalid request format. Expected an array of measurements.'
            });
        }

        MobiletowerModel.insertMany(towers, function(err, createdTowers) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when creating measurements',
                    error: err
                });
            }
    
            return res.status(201).json(createdTowers);
        });
    },

    /**
     * mobileTowerController.update()
     */
    update: function (req, res) {
        var id = req.params.id;

        MobiletowerModel.findOne({_id: id}, function (err, mobileTower) {
            if (err) {
                return res.status(501).json({
                    message: 'Error when getting mobileTower',
                    error: err
                });
            }

            if (!mobileTower) {
                return res.status(404).json({
                    message: 'No such mobileTower'
                });
            }

            mobileTower.location = req.body.location ? req.body.location : mobileTower.location;
			mobileTower.operator = req.body.operator ? req.body.operator : mobileTower.operator;
			mobileTower.type = req.body.type ? req.body.type : mobileTower.type;
            mobileTower.confirmed = req.body.hasOwnProperty('confirmed') ? req.body.confirmed : mobileTower.confirmed;
            mobileTower.locator = req.body.locator ? req.body.locator : mobileTower.locator;
			
            mobileTower.save(function (err, mobileTower) {
                if (err) {
                    return res.status(502).json({
                        message: 'Error when updating mobileTower.',
                        error: err
                    });
                }

                return res.json(mobileTower);
            });
        });
    },

    confirm: function (req, res){
        var id = req.params.id;
        MobiletowerModel.findOneAndUpdate({_id: id}, 
            [{ $set: { confirmed: { $not: "$confirmed" } } } ],
            function (err, mobileTower) {

                if (err) {
                    return res.status(500).json({
                        message: 'Error when getting mobileTower.',
                        error: err
                    });
                }
    
                return res.json(mobileTower);
        }).populate("locator")
    },

    /**
     * mobileTowerController.remove()
     */
    remove: function (req, res) {
        var id = req.params.id;

        MobiletowerModel.findByIdAndRemove(id, function (err, mobileTower) {
            if (err) {
                return res.status(500).json({
                    message: 'Error when deleting the mobileTower.',
                    error: err
                });
            }

            return res.status(204).json();
        });
    },


    addConfirm: async function (req, res) {
        try {
            if (!req.files || !req.files.image) {
                return res.status(400).json({ message: "No image file uploaded" });
            }

            const imageFile = req.files.image;

            const resizedImageBuffer = await sharp(imageFile.data)
                .resize(512, 512) 
                .toFormat("jpeg") 
                .toBuffer();

            const base64Image = resizedImageBuffer.toString("base64");

            const response = await openai.chat.completions.create({
                model: "gpt-4o-mini", 
                messages: [
                    {
                        role: "user",
                        content: [
                            { type: "text", text: "Does this image contain a mobile tower? Answer with yes or no without any punctuation" },
                            {
                                type: "image_url",
                                image_url: {
                                    url: `data:image/jpeg;base64,${base64Image}`,
                                },
                            },
                        ],
                    },
                ],
                max_tokens: 50,
            });

            const gptReply = response.choices[0]?.message?.content?.trim().toLowerCase();
            const confirmed = gptReply === "yes";
            
            const { location, operator, type, locator } = req.body;

            const mobileTower = new MobiletowerModel({
                location,
                operator,
                type,
                confirmed,
                locator,
            });

            const savedTower = await mobileTower.save();
            

            return res.status(201).json({
                message: "Mobile Tower created and confirmed using AI",
                data: { confirmed, savedTower }, 
            });
        } catch (error) {
            console.error("Error in addConfirm function:", error);
            return res.status(500).json({
                message: "Error in addConfirm",
                error: error.message,
            });
        }
    },
};
