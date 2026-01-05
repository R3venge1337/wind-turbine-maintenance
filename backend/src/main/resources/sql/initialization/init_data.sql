INSERT INTO turbine_types (code, osf_limit, wear_increment, rated_power, deicing_cost, description) VALUES
                ('L', 11000, 2,7000.0,800.0, 'Low intensity turbine settings'),
                ('M', 12000, 3, 9000.0,500.0,'Medium intensity turbine settings'),
                ('H', 13000, 5, 10000.0,300.0,'High intensity turbine settings');

-- 1. Turbina typu 'L' (Low Intensity) - Nowa, w pełni sprawna
INSERT INTO turbines (
    id, product_id, settings_id, latitude, longitude, city,
    current_tool_wear, main_severity, current_failure_label,
    description, last_update, created_at, is_active
) VALUES (
             1, 'TURB-L-001', 1, 52.2297, 21.0122, 'Warszawa',
             0, 'NONE', 'HEALTHY',
             'Nowa turbina o niskiej intensywności, zainstalowana na płaskim terenie.',
             NOW(), NOW(), true
         );

-- 2. Turbina typu 'M' (Medium Intensity) - Używana, stan ostrzegawczy
INSERT INTO turbines (
    id, product_id, settings_id, latitude, longitude, city,
    current_tool_wear, main_severity, current_failure_label,
    description, last_update, created_at, is_active
) VALUES (
             2, 'TURB-M-042', 2, 54.3520, 18.6466, 'Gdańsk',
             195, 'NONE', 'HEALTHY',
             'Turbina średniej mocy.',
             NOW(), NOW(), true
         );

-- 3. Turbina typu 'H' (High Intensity) - Wysoka moc, praca w trudnych warunkach
INSERT INTO turbines (
    id, product_id, settings_id, latitude, longitude, city,
    current_tool_wear, main_severity, current_failure_label,
    description, last_update, created_at, is_active
) VALUES (
             3, 'TURB-H-099', 3, 50.0647, 19.9450, 'Kraków',
             45, 'NONE', 'HEALTHY',
             'Wysokowydajna turbina wiatrowa, generująca duże obciążenia mechaniczne.',
             NOW(), NOW(), true
         );