-- Sample Products
INSERT INTO products (product_name, description, price, quantity, image_url) VALUES
                                                                                 ('Laptop', 'High-performance laptop', 1200, 10, 'laptop.jpg'),
                                                                                 ('Headphones', 'Noise-canceling headphones', 200, 50, 'headphones.jpg'),
                                                                                 ('Smartphone', 'Latest model smartphone', 900, 30, 'smartphone.jpg'),
                                                                                 ('Gaming Console', 'Popular gaming console', 400, 25, 'console.jpg'),
                                                                                 ('Smartwatch', 'Wearable smartwatch', 150, 40, 'smartwatch.jpg'),
                                                                                 ('Bluetooth Speaker', 'Portable Bluetooth speaker', 80, 60, 'speaker.jpg'),
                                                                                 ('Tablet', '10-inch tablet', 300, 20, 'tablet.jpg'),
                                                                                 ('Digital Camera', 'High-quality digital camera', 500, 15, 'camera.jpg'),
                                                                                 ('External Hard Drive', '1TB external hard drive', 100, 50, 'harddrive.jpg'),
                                                                                 ('4K Monitor', '27-inch 4K monitor', 350, 10, 'monitor.jpg'),
                                                                                 ('Keyboard', 'Mechanical keyboard', 70, 80, 'keyboard.jpg'),
                                                                                 ('Mouse', 'Wireless optical mouse', 25, 100, 'mouse.jpg'),
                                                                                 ('Webcam', '1080p HD webcam', 60, 30, 'webcam.jpg'),
                                                                                 ('Router', 'Dual-band Wi-Fi router', 120, 25, 'router.jpg'),
                                                                                 ('Fitness Tracker', 'Activity tracking fitness device', 85, 45, 'tracker.jpg'),
                                                                                 ('Smart Thermostat', 'Smart home thermostat', 200, 10, 'thermostat.jpg'),
                                                                                 ('Drone', 'Quadcopter with camera', 400, 5, 'drone.jpg'),
                                                                                 ('VR Headset', 'Virtual reality headset', 250, 15, 'vrheadset.jpg'),
                                                                                 ('Portable Charger', '10,000mAh portable charger', 30, 100, 'charger.jpg'),
                                                                                 ('Coffee Maker', 'Single-serve coffee maker', 100, 20, 'coffeemaker.jpg')
                                                                                 ON CONFLICT (product_name) DO NOTHING;
