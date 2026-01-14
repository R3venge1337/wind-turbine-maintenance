import pandas as pd
import numpy as np
from datetime import datetime, timedelta


def calculate_efficiency(wind_kmh):
    if wind_kmh < 11: return 0.0
    if 11 <= wind_kmh < 18:
        return 0.10 + (wind_kmh - 11) * (0.25 / 7)
    elif 18 <= wind_kmh < 43:
        return 0.35 + (wind_kmh - 18) * (0.50 / 25)
    elif 43 <= wind_kmh <= 90:
        return 0.85 + (wind_kmh - 43) * (0.15 / 47)
    return 0.0


def generate_turbine_data_v7(num_records=50000):
    print(f"Generowanie {num_records} rekordów: Pełna synchronizacja z ProcessTempCalculator.java...")

    turbine_configs = [
        {'id': 'T-L01', 'type': 'L', 'rated_power': 7000.0, 'osf_limit': 11000.0, 'deicing_cost': 800.0, 'tw_inc': 2},
        {'id': 'T-L02', 'type': 'L', 'rated_power': 7000.0, 'osf_limit': 11000.0, 'deicing_cost': 800.0, 'tw_inc': 2},
        {'id': 'T-M01', 'type': 'M', 'rated_power': 9000.0, 'osf_limit': 12000.0, 'deicing_cost': 500.0, 'tw_inc': 3},
        {'id': 'T-M02', 'type': 'M', 'rated_power': 9000.0, 'osf_limit': 12000.0, 'deicing_cost': 500.0, 'tw_inc': 3},
        {'id': 'T-H01', 'type': 'H', 'rated_power': 10000.0, 'osf_limit': 13000.0, 'deicing_cost': 300.0, 'tw_inc': 5},
        {'id': 'T-H02', 'type': 'H', 'rated_power': 10000.0, 'osf_limit': 13000.0, 'deicing_cost': 300.0, 'tw_inc': 5},
    ]

    # Stany turbin (dodajemy last_process_temp dla bezwładności)
    turbine_states = {t['id']: {'tw': 0.0, 'last_pt': None} for t in turbine_configs}
    data = []
    start_time = datetime.now()

    for i in range(num_records):
        config = turbine_configs[i % len(turbine_configs)]
        state = turbine_states[config['id']]

        timestamp = (start_time + timedelta(minutes=i * 10)).strftime("%Y-%m-%d %H:%M:%S")
        local_wind_kmh = np.random.triangular(0, 25, 150)
        local_temp_air = np.random.uniform(-30, 50)

        # 1. Mechanika (Power -> Torque -> RPM)
        eff = calculate_efficiency(local_wind_kmh)
        wind_power = config['rated_power'] * eff
        de_icing_cost = config['deicing_cost'] if local_temp_air < 2.0 else 0.0
        final_power = max(0, wind_power - de_icing_cost) if 11 <= local_wind_kmh <= 90 else 0.0

        torque = 0.0
        rpm = 0.0
        if final_power > 0:
            torque = 40.0 + (np.random.normal() * 10.0)
            if torque < 5.0: torque = 5.0
            rpm = (final_power * 9.5493) / torque

        # 2. Logika z Twojej klasy ProcessTempCalculator.java
        ref_torque = torque if torque > 10 else 40.0
        max_rpm_for_model = (config['rated_power'] * 9.5493) / ref_torque

        load_factor = (rpm / max_rpm_for_model) if max_rpm_for_model > 0 else 0
        heat_gain = load_factor * 15.0

        target_temp = local_temp_air + heat_gain + (np.random.normal() * 0.5)

        # Start od temp. otoczenia (Twoje getLastProcessTemp)
        prev_pt = state['last_pt'] if state['last_pt'] is not None else local_temp_air
        current_process_temp = (prev_pt * 0.7) + (target_temp * 0.3)
        state['last_pt'] = current_process_temp

        delta_t = current_process_temp - local_temp_air

        # 3. Logika Label ID
        label_id = 0
        reset_required = False

        # FAILURE
        if delta_t < 8.6 and rpm < 1380:
            label_id = 2  # HDF
        elif final_power < 1500 or final_power > 9500:
            label_id = 4  # PWF
        elif torque * state['tw'] > config['osf_limit']:
            label_id = 6; reset_required = True
        elif state['tw'] >= 240:
            label_id = 8; reset_required = True
        elif state['tw'] >= 200 and np.random.random() < 0.15:
            label_id = 8; reset_required = True

        # CAUTION
        elif label_id == 0:
            if delta_t < 10.0 and rpm < 1500:
                label_id = 1
            elif final_power < 2000 or final_power > 9000:
                label_id = 3
            elif torque * state['tw'] > config['osf_limit'] * 0.8:
                label_id = 5
            elif state['tw'] > 190:
                label_id = 7

        data.append([
            timestamp,  # 1
            config['id'],  # 2
            config['type'],  # 3
            round(local_wind_kmh, 2),  # 4
            round(local_temp_air, 2),  # 5
            round(current_process_temp, 2),  # 6
            round(rpm, 0),  # 7
            round(final_power, 2),  # 8
            round(torque, 2),  # 9
            round(state['tw'], 2),  # 10
            label_id  # 11
        ])

        state['tw'] += config['tw_inc']
        if reset_required: state['tw'] = 0.0

    columns = ["timestamp", "turbine_id", "turbine_type", "wind_kmh", "local_temp", "process_temp", "rpm", "power",
               "torque", "tool_wear", "label_id"]
    df = pd.DataFrame(data, columns=columns)
    df.to_csv("turbine_fleet_final_v11.csv", index=False)

    print("\nGenerator gotowy i zsynchronizowany z Javą!")
    print(df['label_id'].value_counts().sort_index())


if __name__ == "__main__":
    generate_turbine_data_v7()