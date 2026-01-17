import random
from datetime import datetime

# Koordynaty bazowe (Gdańsk)
base_lat = 54.3520
base_lon = 18.6466

types = [
    {'id': 1, 'code': 'L', 'desc': 'Niska intensywność - Pomorze'},
    {'id': 2, 'code': 'M', 'desc': 'Średnia intensywność - Wybrzeże'},
    {'id': 3, 'code': 'H', 'desc': 'Wysoka moc - Farma morska/przybrzeżna'}
]


def generate_inserts(count=50):
    sql_statements = []

    for i in range(4, count + 4):  # Zaczynamy od id=4, bo 1-3 już masz
        t_type = random.choice(types)
        # Mała wariancja koordynatów (ok. 0.1 stopnia to ok. 11km)
        lat = base_lat + random.uniform(-0.25, 0.25)
        lon = base_lon + random.uniform(-0.35, 0.35)

        # Losowe zużycie narzędzi (nowe lub lekko używane)
        tool_wear = random.randint(0, 150)
        prod_id = f"TURB-{t_type['code']}-{i:03d}"

        sql = (
            f"INSERT INTO turbines (id, product_id, settings_id, latitude, longitude, city, "
            f"current_tool_wear, main_severity, current_failure_label, description, "
            f"last_update, created_at, is_active) "
            f"VALUES ({i}, '{prod_id}', {t_type['id']}, {lat:.4f}, {lon:.4f}, 'Gdańsk (Okolice)', "
            f"{tool_wear}, 'NONE', 'HEALTHY', '{t_type['desc']}', NOW(), NOW(), true);"
        )
        sql_statements.append(sql)

    return "\n".join(sql_statements)


# Generowanie i wyświetlenie
print("-- AUTO-GENERATED TURBINES NEAR GDAŃSK")
print(generate_inserts(50))