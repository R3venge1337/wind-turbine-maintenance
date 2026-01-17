INSERT INTO turbine_types ( code, osf_limit, wear_increment, rated_power, deicing_cost, description) VALUES
                ('L', 11000, 2,7000.0,800.0, 'Low intensity turbine settings'),
                ('M', 12000, 3, 9000.0,500.0,'Medium intensity turbine settings'),
                ('H', 13000, 5, 10000.0,300.0,'High intensity turbine settings');

-- 1. Turbina typu 'L' (Low Intensity) - Nowa, w pełni sprawna
INSERT INTO turbines (
    product_id, settings_id, latitude, longitude, city,
    current_tool_wear, main_severity, current_failure_label,
    description, last_update, created_at, is_active
) VALUES (
            'TURB-L-001', 1, 52.2297, 21.0122, 'Warszawa',
             0, 'NONE', 'HEALTHY',
             'Nowa turbina o niskiej intensywności, zainstalowana na płaskim terenie.',
             NOW(), NOW(), true
         );

-- 2. Turbina typu 'M' (Medium Intensity) - Używana, stan ostrzegawczy
INSERT INTO turbines (
    product_id, settings_id, latitude, longitude, city,
    current_tool_wear, main_severity, current_failure_label,
    description, last_update, created_at, is_active
) VALUES (
            'TURB-M-053', 2, 54.3520, 18.6466, 'Gdańsk',
             195, 'NONE', 'HEALTHY',
             'Turbina średniej mocy.',
             NOW(), NOW(), true
         );

-- 3. Turbina typu 'H' (High Intensity) - Wysoka moc, praca w trudnych warunkach
INSERT INTO turbines (
    product_id, settings_id, latitude, longitude, city,
    current_tool_wear, main_severity, current_failure_label,
    description, last_update, created_at, is_active
) VALUES (
             'TURB-H-099', 3, 50.0647, 19.9450, 'Kraków',
             45, 'NONE', 'HEALTHY',
             'Wysokowydajna turbina wiatrowa, generująca duże obciążenia mechaniczne.',
             NOW(), NOW(), true
         );
INSERT INTO turbines (product_id, settings_id, latitude, longitude, city, current_tool_wear, main_severity, current_failure_label, description, last_update, created_at, is_active) VALUES ( 'TURB-M-004', 2, 54.1847, 18.6137, 'Gdańsk (Okolice)', 149, 'NONE', 'HEALTHY', 'Średnia intensywność - Wybrzeże', NOW(), NOW(), true);
INSERT INTO turbines ( product_id, settings_id, latitude, longitude, city, current_tool_wear, main_severity, current_failure_label, description, last_update, created_at, is_active) VALUES ( 'TURB-M-005', 2, 54.2366, 18.3742, 'Gdańsk (Okolice)', 77, 'NONE', 'HEALTHY', 'Średnia intensywność - Wybrzeże', NOW(), NOW(), true);
INSERT INTO turbines ( product_id, settings_id, latitude, longitude, city, current_tool_wear, main_severity, current_failure_label, description, last_update, created_at, is_active) VALUES ( 'TURB-M-006', 2, 54.3990, 18.9104, 'Gdańsk (Okolice)', 21, 'NONE', 'HEALTHY', 'Średnia intensywność - Wybrzeże', NOW(), NOW(), true);
INSERT INTO turbines (product_id, settings_id, latitude, longitude, city, current_tool_wear, main_severity, current_failure_label, description, last_update, created_at, is_active) VALUES ( 'TURB-M-007', 2, 54.1703, 18.3988, 'Gdańsk (Okolice)', 75, 'NONE', 'HEALTHY', 'Średnia intensywność - Wybrzeże', NOW(), NOW(), true);
INSERT INTO turbines (product_id, settings_id, latitude, longitude, city, current_tool_wear, main_severity, current_failure_label, description, last_update, created_at, is_active) VALUES ( 'TURB-L-008', 1, 54.1167, 18.9475, 'Gdańsk (Okolice)', 74, 'NONE', 'HEALTHY', 'Niska intensywność - Pomorze', NOW(), NOW(), true);
INSERT INTO turbines (product_id, settings_id, latitude, longitude, city, current_tool_wear, main_severity, current_failure_label, description, last_update, created_at, is_active) VALUES ( 'TURB-L-009', 1, 54.3655, 18.6770, 'Gdańsk (Okolice)', 122, 'NONE', 'HEALTHY', 'Niska intensywność - Pomorze', NOW(), NOW(), true);
INSERT INTO turbines (product_id, settings_id, latitude, longitude, city, current_tool_wear, main_severity, current_failure_label, description, last_update, created_at, is_active) VALUES ( 'TURB-H-010', 3, 54.4737, 18.6570, 'Gdańsk (Okolice)', 22, 'NONE', 'HEALTHY', 'Wysoka moc - Farma morska/przybrzeżna', NOW(), NOW(), true);
INSERT INTO turbines (product_id, settings_id, latitude, longitude, city, current_tool_wear, main_severity, current_failure_label, description, last_update, created_at, is_active) VALUES ( 'TURB-M-011', 2, 54.5138, 18.6014, 'Gdańsk (Okolice)', 43, 'NONE', 'HEALTHY', 'Średnia intensywność - Wybrzeże', NOW(), NOW(), true);
INSERT INTO turbines (product_id, settings_id, latitude, longitude, city, current_tool_wear, main_severity, current_failure_label, description, last_update, created_at, is_active) VALUES ( 'TURB-M-012', 2, 54.5053, 18.8295, 'Gdańsk (Okolice)', 19, 'NONE', 'HEALTHY', 'Średnia intensywność - Wybrzeże', NOW(), NOW(), true);
INSERT INTO turbines (product_id, settings_id, latitude, longitude, city, current_tool_wear, main_severity, current_failure_label, description, last_update, created_at, is_active) VALUES ( 'TURB-M-013', 2, 54.5991, 18.3655, 'Gdańsk (Okolice)', 141, 'NONE', 'HEALTHY', 'Średnia intensywność - Wybrzeże', NOW(), NOW(), true);
INSERT INTO turbines (product_id, settings_id, latitude, longitude, city, current_tool_wear, main_severity, current_failure_label, description, last_update, created_at, is_active) VALUES ( 'TURB-L-014', 1, 54.4841, 18.6957, 'Gdańsk (Okolice)', 70, 'NONE', 'HEALTHY', 'Niska intensywność - Pomorze', NOW(), NOW(), true);
INSERT INTO turbines (product_id, settings_id, latitude, longitude, city, current_tool_wear, main_severity, current_failure_label, description, last_update, created_at, is_active) VALUES ( 'TURB-M-015', 2, 54.4788, 18.7798, 'Gdańsk (Okolice)', 102, 'NONE', 'HEALTHY', 'Średnia intensywność - Wybrzeże', NOW(), NOW(), true);
INSERT INTO turbines (product_id, settings_id, latitude, longitude, city, current_tool_wear, main_severity, current_failure_label, description, last_update, created_at, is_active) VALUES ( 'TURB-L-016', 1, 54.5199, 18.4407, 'Gdańsk (Okolice)', 129, 'NONE', 'HEALTHY', 'Niska intensywność - Pomorze', NOW(), NOW(), true);
INSERT INTO turbines (product_id, settings_id, latitude, longitude, city, current_tool_wear, main_severity, current_failure_label, description, last_update, created_at, is_active) VALUES ( 'TURB-M-017', 2, 54.4217, 18.8054, 'Gdańsk (Okolice)', 18, 'NONE', 'HEALTHY', 'Średnia intensywność - Wybrzeże', NOW(), NOW(), true);
INSERT INTO turbines (product_id, settings_id, latitude, longitude, city, current_tool_wear, main_severity, current_failure_label, description, last_update, created_at, is_active) VALUES ( 'TURB-H-018', 3, 54.3209, 18.4569, 'Gdańsk (Okolice)', 65, 'NONE', 'HEALTHY', 'Wysoka moc - Farma morska/przybrzeżna', NOW(), NOW(), true);
INSERT INTO turbines (product_id, settings_id, latitude, longitude, city, current_tool_wear, main_severity, current_failure_label, description, last_update, created_at, is_active) VALUES ( 'TURB-M-019', 2, 54.4504, 18.8743, 'Gdańsk (Okolice)', 57, 'NONE', 'HEALTHY', 'Średnia intensywność - Wybrzeże', NOW(), NOW(), true);
INSERT INTO turbines (product_id, settings_id, latitude, longitude, city, current_tool_wear, main_severity, current_failure_label, description, last_update, created_at, is_active) VALUES ( 'TURB-L-020', 1, 54.3712, 18.8463, 'Gdańsk (Okolice)', 106, 'NONE', 'HEALTHY', 'Niska intensywność - Pomorze', NOW(), NOW(), true);
INSERT INTO turbines (product_id, settings_id, latitude, longitude, city, current_tool_wear, main_severity, current_failure_label, description, last_update, created_at, is_active) VALUES ( 'TURB-M-021', 2, 54.3941, 18.5518, 'Gdańsk (Okolice)', 11, 'NONE', 'HEALTHY', 'Średnia intensywność - Wybrzeże', NOW(), NOW(), true);
INSERT INTO turbines (product_id, settings_id, latitude, longitude, city, current_tool_wear, main_severity, current_failure_label, description, last_update, created_at, is_active) VALUES ( 'TURB-H-022', 3, 54.3200, 18.9351, 'Gdańsk (Okolice)', 63, 'NONE', 'HEALTHY', 'Wysoka moc - Farma morska/przybrzeżna', NOW(), NOW(), true);
INSERT INTO turbines (product_id, settings_id, latitude, longitude, city, current_tool_wear, main_severity, current_failure_label, description, last_update, created_at, is_active) VALUES ( 'TURB-L-023', 1, 54.1443, 18.5962, 'Gdańsk (Okolice)', 74, 'NONE', 'HEALTHY', 'Niska intensywność - Pomorze', NOW(), NOW(), true);
INSERT INTO turbines (product_id, settings_id, latitude, longitude, city, current_tool_wear, main_severity, current_failure_label, description, last_update, created_at, is_active) VALUES ( 'TURB-M-024', 2, 54.5111, 18.5390, 'Gdańsk (Okolice)', 9, 'NONE', 'HEALTHY', 'Średnia intensywność - Wybrzeże', NOW(), NOW(), true);
INSERT INTO turbines (product_id, settings_id, latitude, longitude, city, current_tool_wear, main_severity, current_failure_label, description, last_update, created_at, is_active) VALUES ( 'TURB-M-025', 2, 54.4216, 18.6788, 'Gdańsk (Okolice)', 74, 'NONE', 'HEALTHY', 'Średnia intensywność - Wybrzeże', NOW(), NOW(), true);
INSERT INTO turbines (product_id, settings_id, latitude, longitude, city, current_tool_wear, main_severity, current_failure_label, description, last_update, created_at, is_active) VALUES ( 'TURB-M-026', 2, 54.2464, 18.4376, 'Gdańsk (Okolice)', 123, 'NONE', 'HEALTHY', 'Średnia intensywność - Wybrzeże', NOW(), NOW(), true);
INSERT INTO turbines (product_id, settings_id, latitude, longitude, city, current_tool_wear, main_severity, current_failure_label, description, last_update, created_at, is_active) VALUES ( 'TURB-L-027', 1, 54.3812, 18.5547, 'Gdańsk (Okolice)', 15, 'NONE', 'HEALTHY', 'Niska intensywność - Pomorze', NOW(), NOW(), true);
INSERT INTO turbines (product_id, settings_id, latitude, longitude, city, current_tool_wear, main_severity, current_failure_label, description, last_update, created_at, is_active) VALUES ( 'TURB-L-028', 1, 54.5387, 18.9607, 'Gdańsk (Okolice)', 75, 'NONE', 'HEALTHY', 'Niska intensywność - Pomorze', NOW(), NOW(), true);
INSERT INTO turbines (product_id, settings_id, latitude, longitude, city, current_tool_wear, main_severity, current_failure_label, description, last_update, created_at, is_active) VALUES ( 'TURB-L-029', 1, 54.2893, 18.4348, 'Gdańsk (Okolice)', 1, 'NONE', 'HEALTHY', 'Niska intensywność - Pomorze', NOW(), NOW(), true);
INSERT INTO turbines (product_id, settings_id, latitude, longitude, city, current_tool_wear, main_severity, current_failure_label, description, last_update, created_at, is_active) VALUES ( 'TURB-L-030', 1, 54.1720, 18.4722, 'Gdańsk (Okolice)', 98, 'NONE', 'HEALTHY', 'Niska intensywność - Pomorze', NOW(), NOW(), true);
INSERT INTO turbines (product_id, settings_id, latitude, longitude, city, current_tool_wear, main_severity, current_failure_label, description, last_update, created_at, is_active) VALUES ( 'TURB-L-031', 1, 54.1901, 18.3407, 'Gdańsk (Okolice)', 149, 'NONE', 'HEALTHY', 'Niska intensywność - Pomorze', NOW(), NOW(), true);
INSERT INTO turbines (product_id, settings_id, latitude, longitude, city, current_tool_wear, main_severity, current_failure_label, description, last_update, created_at, is_active) VALUES ( 'TURB-H-032', 3, 54.5414, 18.3687, 'Gdańsk (Okolice)', 68, 'NONE', 'HEALTHY', 'Wysoka moc - Farma morska/przybrzeżna', NOW(), NOW(), true);
INSERT INTO turbines (product_id, settings_id, latitude, longitude, city, current_tool_wear, main_severity, current_failure_label, description, last_update, created_at, is_active) VALUES ('TURB-L-033', 1, 54.4154, 18.7224, 'Gdańsk (Okolice)', 139, 'NONE', 'HEALTHY', 'Niska intensywność - Pomorze', NOW(), NOW(), true);
INSERT INTO turbines (product_id, settings_id, latitude, longitude, city, current_tool_wear, main_severity, current_failure_label, description, last_update, created_at, is_active) VALUES ( 'TURB-M-034', 2, 54.2299, 18.8740, 'Gdańsk (Okolice)', 119, 'NONE', 'HEALTHY', 'Średnia intensywność - Wybrzeże', NOW(), NOW(), true);
INSERT INTO turbines (product_id, settings_id, latitude, longitude, city, current_tool_wear, main_severity, current_failure_label, description, last_update, created_at, is_active) VALUES ( 'TURB-L-035', 1, 54.2232, 18.7138, 'Gdańsk (Okolice)', 86, 'NONE', 'HEALTHY', 'Niska intensywność - Pomorze', NOW(), NOW(), true);
INSERT INTO turbines (product_id, settings_id, latitude, longitude, city, current_tool_wear, main_severity, current_failure_label, description, last_update, created_at, is_active) VALUES ( 'TURB-M-036', 2, 54.5172, 18.7678, 'Gdańsk (Okolice)', 132, 'NONE', 'HEALTHY', 'Średnia intensywność - Wybrzeże', NOW(), NOW(), true);
INSERT INTO turbines (product_id, settings_id, latitude, longitude, city, current_tool_wear, main_severity, current_failure_label, description, last_update, created_at, is_active) VALUES ( 'TURB-M-037', 2, 54.1170, 18.9202, 'Gdańsk (Okolice)', 134, 'NONE', 'HEALTHY', 'Średnia intensywność - Wybrzeże', NOW(), NOW(), true);
INSERT INTO turbines (product_id, settings_id, latitude, longitude, city, current_tool_wear, main_severity, current_failure_label, description, last_update, created_at, is_active) VALUES ( 'TURB-L-038', 1, 54.5487, 18.6122, 'Gdańsk (Okolice)', 125, 'NONE', 'HEALTHY', 'Niska intensywność - Pomorze', NOW(), NOW(), true);
INSERT INTO turbines (product_id, settings_id, latitude, longitude, city, current_tool_wear, main_severity, current_failure_label, description, last_update, created_at, is_active) VALUES ( 'TURB-M-039', 2, 54.3691, 18.4859, 'Gdańsk (Okolice)', 70, 'NONE', 'HEALTHY', 'Średnia intensywność - Wybrzeże', NOW(), NOW(), true);
INSERT INTO turbines (product_id, settings_id, latitude, longitude, city, current_tool_wear, main_severity, current_failure_label, description, last_update, created_at, is_active) VALUES ( 'TURB-M-040', 2, 54.2156, 18.8065, 'Gdańsk (Okolice)', 21, 'NONE', 'HEALTHY', 'Średnia intensywność - Wybrzeże', NOW(), NOW(), true);
INSERT INTO turbines (product_id, settings_id, latitude, longitude, city, current_tool_wear, main_severity, current_failure_label, description, last_update, created_at, is_active) VALUES ( 'TURB-H-041', 3, 54.2870, 18.3528, 'Gdańsk (Okolice)', 141, 'NONE', 'HEALTHY', 'Wysoka moc - Farma morska/przybrzeżna', NOW(), NOW(), true);
INSERT INTO turbines (product_id, settings_id, latitude, longitude, city, current_tool_wear, main_severity, current_failure_label, description, last_update, created_at, is_active) VALUES ( 'TURB-M-042', 2, 54.1149, 18.9012, 'Gdańsk (Okolice)', 106, 'NONE', 'HEALTHY', 'Średnia intensywność - Wybrzeże', NOW(), NOW(), true);
INSERT INTO turbines (product_id, settings_id, latitude, longitude, city, current_tool_wear, main_severity, current_failure_label, description, last_update, created_at, is_active) VALUES ( 'TURB-H-043', 3, 54.3517, 18.4740, 'Gdańsk (Okolice)', 50, 'NONE', 'HEALTHY', 'Wysoka moc - Farma morska/przybrzeżna', NOW(), NOW(), true);
INSERT INTO turbines (product_id, settings_id, latitude, longitude, city, current_tool_wear, main_severity, current_failure_label, description, last_update, created_at, is_active) VALUES ( 'TURB-H-044', 3, 54.3855, 18.7652, 'Gdańsk (Okolice)', 22, 'NONE', 'HEALTHY', 'Wysoka moc - Farma morska/przybrzeżna', NOW(), NOW(), true);
INSERT INTO turbines (product_id, settings_id, latitude, longitude, city, current_tool_wear, main_severity, current_failure_label, description, last_update, created_at, is_active) VALUES ( 'TURB-H-045', 3, 54.5577, 18.4165, 'Gdańsk (Okolice)', 122, 'NONE', 'HEALTHY', 'Wysoka moc - Farma morska/przybrzeżna', NOW(), NOW(), true);
INSERT INTO turbines (product_id, settings_id, latitude, longitude, city, current_tool_wear, main_severity, current_failure_label, description, last_update, created_at, is_active) VALUES ( 'TURB-L-046', 1, 54.1835, 18.9816, 'Gdańsk (Okolice)', 134, 'NONE', 'HEALTHY', 'Niska intensywność - Pomorze', NOW(), NOW(), true);
INSERT INTO turbines (product_id, settings_id, latitude, longitude, city, current_tool_wear, main_severity, current_failure_label, description, last_update, created_at, is_active) VALUES ( 'TURB-M-047', 2, 54.3961, 18.5597, 'Gdańsk (Okolice)', 126, 'NONE', 'HEALTHY', 'Średnia intensywność - Wybrzeże', NOW(), NOW(), true);
INSERT INTO turbines (product_id, settings_id, latitude, longitude, city, current_tool_wear, main_severity, current_failure_label, description, last_update, created_at, is_active) VALUES ( 'TURB-M-048', 2, 54.4591, 18.9292, 'Gdańsk (Okolice)', 17, 'NONE', 'HEALTHY', 'Średnia intensywność - Wybrzeże', NOW(), NOW(), true);
INSERT INTO turbines (product_id, settings_id, latitude, longitude, city, current_tool_wear, main_severity, current_failure_label, description, last_update, created_at, is_active) VALUES ( 'TURB-L-049', 1, 54.3423, 18.9914, 'Gdańsk (Okolice)', 94, 'NONE', 'HEALTHY', 'Niska intensywność - Pomorze', NOW(), NOW(), true);
INSERT INTO turbines (product_id, settings_id, latitude, longitude, city, current_tool_wear, main_severity, current_failure_label, description, last_update, created_at, is_active) VALUES ( 'TURB-M-050', 2, 54.1153, 18.8000, 'Gdańsk (Okolice)', 133, 'NONE', 'HEALTHY', 'Średnia intensywność - Wybrzeże', NOW(), NOW(), true);
INSERT INTO turbines (product_id, settings_id, latitude, longitude, city, current_tool_wear, main_severity, current_failure_label, description, last_update, created_at, is_active) VALUES ('TURB-H-051', 3, 54.5592, 18.7593, 'Gdańsk (Okolice)', 101, 'NONE', 'HEALTHY', 'Wysoka moc - Farma morska/przybrzeżna', NOW(), NOW(), true);
INSERT INTO turbines (product_id, settings_id, latitude, longitude, city, current_tool_wear, main_severity, current_failure_label, description, last_update, created_at, is_active) VALUES ('TURB-M-052', 2, 54.5483, 18.9389, 'Gdańsk (Okolice)', 108, 'NONE', 'HEALTHY', 'Średnia intensywność - Wybrzeże', NOW(), NOW(), true);
INSERT INTO turbines (product_id, settings_id, latitude, longitude, city, current_tool_wear, main_severity, current_failure_label, description, last_update, created_at, is_active) VALUES ( 'TURB-H-053', 3, 54.5590, 18.6472, 'Gdańsk (Okolice)', 104, 'NONE', 'HEALTHY', 'Wysoka moc - Farma morska/przybrzeżna', NOW(), NOW(), true);
