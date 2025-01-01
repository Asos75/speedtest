"""
Glavni program (app.py), ki naloži model in omogoča klic funkcije za razpoznavo.
"""

from flask import Flask, request, jsonify
import recognize

app = Flask(__name__)

# Komentar (SI): Inicializiramo aplikacijo in v ozadju naložimo model
@app.route("/predict", methods=["POST"])
def predict():
    # Komentar (SI): Tu bi prejeli sliko iz POST zahteve in jo poslali naprej k razpoznavanju
    # Za primer, predpostavimo, da imamo pot do slike v parametrih
    image_path = request.form.get("image_path", "")
    result = recognize.recognize_tower(image_path)
    return jsonify({"rezultat": result})

if __name__ == "__main__":
    app.run(debug=True)