import os
import cv2
import json
import re

RAW_DIR = "Data/Raw"
PROC_DIR = "Data/Processed"
BBOX_FILE = "Data/bounding_boxes_list.json"

if os.path.exists(BBOX_FILE):
    with open(BBOX_FILE, "r") as f:
        bbox_data = json.load(f)
else:
    bbox_data = {}

def get_number_from_filename(filename):
    # Vrne prvo številko iz niza
    numbers = re.findall(r'\d+', filename)
    return int(numbers[0]) if numbers else 0

# Funkcija za omejitev koordinat
def clamp(x, y, w, h, maxW, maxH):
    if x < 0: x = 0
    if y < 0: y = 0
    if x + w > maxW: x = maxW - w
    if y + h > maxH: y = maxH - h
    return int(x), int(y), int(w), int(h)

def save_with_rectangle(inp, outp, coords):
    img = cv2.imread(inp)
    if img is None:
        return
    x1, y1, x2, y2 = coords
    cv2.rectangle(img, (x1, y1), (x2, y2), (0, 0, 255), 2)
    cv2.imwrite(outp, img)

def main():
    files = [f for f in sorted(os.listdir(RAW_DIR)) if f.lower().endswith(".jpg")]
    # Odkomentiraš če želiš, da se slike obdelajo po vrsti
    #files.sort(key=get_number_from_filename)

    total = len(files)
    index = 0
    for i, f in enumerate(files):
        if not os.path.exists(os.path.join(PROC_DIR, f)):
            index = i
            break
    done = False

    while not done and 0 <= index < total:
        image_name = files[index]
        image_path = os.path.join(RAW_DIR, image_name)
        target_path = os.path.join(PROC_DIR, image_name)

        print(f"Izdelujem {index+1}/{total}: {image_name}")
        print(f"Oznacenih: {len(bbox_data)}/{total}")
        print("Navodila: C=Potrdi, R=Reset, N=Naslednja, P=Prejsnja, E/Q/ESC=Izhod")

        orig = cv2.imread(image_path)
        if orig is None:
            print("Napaka pri branju slike.")
            index += 1
            continue

        hImg, wImg = orig.shape[:2]

        if image_name not in bbox_data:
            x,y,w,h = cv2.selectROI("Oznaci", orig, fromCenter=False, showCrosshair=True)
            cv2.destroyWindow("Oznaci")
            if w <= 0 or h <= 0:
                print("Ni oznaceno.")
                index += 1
                continue
            bbox_data[image_name] = [int(x), int(y), int(x+w), int(y+h)]

        # Pripravi spremenljivke za urejanje
        x1, y1, x2, y2 = bbox_data[image_name]
        boxX, boxY = x1, y1
        boxW, boxH = x2 - x1, y2 - y1

        while True:
            temp = orig.copy()
            cv2.rectangle(temp, (boxX, boxY), (boxX+boxW, boxY+boxH), (0, 0, 255), 2)
            cv2.imshow("Uredi", temp)
            key = cv2.waitKey(50) & 0xFF

            if key in [ord('C'), ord('c')]:
                bbox_data[image_name] = [boxX, boxY, boxX+boxW, boxY+boxH]
                with open(BBOX_FILE, "w") as f:
                    json.dump(bbox_data, f, indent=2)
                print("Potrjeno.")
                save_with_rectangle(image_path, target_path, bbox_data[image_name])
                cv2.destroyAllWindows()
                index += 1 
                break
            elif key in [ord('R'), ord('r')]:
                x,y,w,h = cv2.selectROI("Ponovi", orig, fromCenter=False, showCrosshair=True)
                cv2.destroyWindow("Ponovi")
                if w>0 and h>0:
                    bbox_data[image_name] = [int(x), int(y), int(x+w), int(y+h)]
                x1, y1, x2, y2 = bbox_data[image_name]
                boxX,boxY = x1,y1
                boxW,boxH = x2-x1, x2-x1
            elif key in [ord('N'), ord('n')]:
                index += 1
                break
            elif key in [ord('P'), ord('p')]:
                index -= 1
                break
            elif key in [27, ord('q'), ord('Q'), ord('e'), ord('E')]:
                print("Izhod.")
                done = True
                break
            elif key == 49:
                boxX -= 1
            elif key == 51:
                boxX += 1
            elif key == 53:
                boxY -= 1
            elif key == 50:
                boxY += 1
            elif key in [ord('w'), ord('W')]:
                boxH -= 1
            elif key in [ord('s'), ord('S')]:
                boxH += 1
            elif key in [ord('a'), ord('A')]:
                boxW -= 1
            elif key in [ord('d'), ord('D')]:
                boxW += 1

            if boxW<1: boxW=1
            if boxH<1: boxH=1
            boxX, boxY, boxW, boxH = clamp(boxX, boxY, boxW, boxH, wImg, hImg)

        # if image_name in bbox_data and not os.path.exists(target_path) and not done:
        #     save_with_rectangle(image_path, target_path, bbox_data[image_name])
        #     print(f"Shranjeno v {target_path}")

    cv2.destroyAllWindows()
    done_count = sum(1 for f in files if os.path.exists(os.path.join(PROC_DIR, f)))
    print(f"Obdelano: {done_count}/{total}")

if __name__=="__main__":
    main()