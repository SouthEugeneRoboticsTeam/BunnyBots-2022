#!/usr/bin/env python3

import time
import math

from pupil_apriltags import Detector
import cv2
import numpy as np
import os

from networktables import NetworkTables

while True:
    NetworkTables.initialize(server='roborio-2521-frc.local')

    cap = cv2.VideoCapture(0)

    vision_table = NetworkTables.getTable('Vision')

    cam_params = [685.55496655, 696.9549483, 329.08110703, 191.24604484]
    detector = Detector(families='tag36h11')

    # replace with actual check that networktables is connected
    time.sleep(10.0)

    # try is for just in case
    try:
        # Some use tag ids and also maybe send multiple instead of closest
        while True:
            start_time = time.time()

            ret, input_img = cap.read()

            if not ret:
                continue

            detections = detector.detect(cv2.cvtColor(input_img, cv2.COLOR_BGR2GRAY), estimate_tag_pose=True,
                                         camera_params=cam_params, tag_size=0.2)

            is_detection = len(detections) > 0
            if is_detection:
                closest = max(detections,
                              key=lambda detection: detection.pose_t[0] ** 2 + detection.pose_t[1] ** 2 + detection.pose_t[
                                  2] ** 2)

                vision_table.putNumberArray('Position', [value[0] for value in closest.pose_t])
                vision_table.putNumberArray('Rotation', [value for row in closest.pose_R for value in row])
                vision_table.putNumber('Last Update', start_time)
                vision_table.putBoolean('Is Target', True)

                for detection in detections:
                    dis = math.sqrt(detection.pose_t[0] ** 2 + detection.pose_t[1] ** 2 + detection.pose_t[2] ** 2)
            else:
                vision_table.putBoolean('Is Target', False)

            processing_time = time.time() - start_time
            fps = 1 / processing_time
            print(str(round(fps, 2)))

    except Exception as e:
        try:
            vision_table.putBoolean('Is Target', False)
        except:
            pass

        print(e)
