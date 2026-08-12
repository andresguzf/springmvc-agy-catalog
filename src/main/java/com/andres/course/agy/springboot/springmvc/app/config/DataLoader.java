package com.andres.course.agy.springboot.springmvc.app.config;

import com.andres.course.agy.springboot.springmvc.app.models.Product;
import com.andres.course.agy.springboot.springmvc.app.repositories.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

import jakarta.persistence.EntityManager;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final EntityManager entityManager;

    public DataLoader(ProductRepository productRepository, EntityManager entityManager) {
        this.productRepository = productRepository;
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        try {
            entityManager.createNativeQuery("TRUNCATE TABLE products RESTART IDENTITY CASCADE").executeUpdate();
        } catch (Exception e) {
            productRepository.deleteAllInBatch();
        }

        LocalDateTime now = LocalDateTime.now();

        List<Product> products = List.of(
                new Product("Laptop Gaming ASUS ROG", "Laptop de alto rendimiento con RTX 4070, 32GB RAM y Ryzen 9", 1499.99, 12, now.minusDays(14)),
                new Product("Teclado Mecánico RGB", "Teclado mecánico con switches Cherry MX Red e iluminación RGB", 89.50, 45, now.minusDays(14)),
                new Product("Monitor UltraWide 34\"", "Monitor curvo 144Hz 1ms con soporte HDR10 y FreeSync", 450.00, 8, now.minusDays(13)),
                new Product("Ratón Inalámbrico Pro", "Ratón ergonómico con sensor óptico de 26,000 DPI y carga rápida", 65.00, 30, now.minusDays(12)),
                new Product("Auriculares Bluetooth ANC", "Auriculares con cancelación activa de ruido y 30 horas de autonomía", 199.00, 15, now.minusDays(11)),
                new Product("Silla Gaming Ergonómica", "Silla ajustarle con cojín lumbar de espuma con memoria", 249.99, 10, now.minusDays(10)),
                new Product("Webcam 4K Pro Ultra", "Cámara web 4K con micrófono dual estéreo y enfoque automático", 129.90, 22, now.minusDays(9)),
                new Product("Micrófono USB Studio", "Micrófono de condensador profesional con patrón cardioide", 110.00, 18, now.minusDays(8)),
                new Product("SSD NVMe 2TB High Speed", "Disco de estado sólido PCIe 4.0 con velocidades de 7300 MB/s", 169.50, 40, now.minusDays(7)),
                new Product("Memoria RAM DDR5 32GB", "Kit de 2x16GB a 6000MHz CL30 con perfil Expo y XMP 3.0", 135.00, 25, now.minusDays(6)),
                new Product("Tarjeta Gráfica RTX 4080", "GPU de 16GB GDDR6X para gaming 4K y renderizado acelerado por IA", 1199.99, 5, now.minusDays(5)),
                new Product("Procesador Intel Core i9", "Procesador de 24 núcleos y 32 hilos hasta 5.8 GHz", 589.00, 14, now.minusDays(4)),
                new Product("Placa Base Z790 WiFi", "Placa madre ATX con PCIe 5.0, WiFi 6E y triple ranura M.2", 280.00, 11, now.minusDays(3)),
                new Product("Fuente 850W Gold Modular", "Fuente de alimentación certificación 80 Plus Gold con cables planos", 139.99, 20, now.minusDays(2)),
                new Product("Refrigeración Líquida 360mm", "Kit AIO con radiador de 360mm e iluminación ARGB direccionable", 145.00, 16, now.minusDays(1)),
                new Product("Alfombrilla XL Desk Mat", "Superficie de tela de alta precisión 900x400mm antideslizante", 29.99, 50, now),
                new Product("Soporte Brazo Doble Monitor", "Soporte articulado a gas para dos monitores de hasta 32 pulgadas", 79.90, 19, now.minusDays(14)),
                new Product("Altavoces Estéreo Hi-Fi", "Sistema de altavoces de escritorio 2.0 con conexión Bluetooth 5.0", 85.00, 13, now.minusDays(12)),
                new Product("Capturadora de Video 4K60", "Dispositivo de captura USB 3.0 para streaming en directo a 60fps", 159.00, 9, now.minusDays(10)),
                new Product("Hub USB-C 10 en 1", "Adaptador multiport con HDMI 4K, lectores SD, Ethernet y PD 100W", 49.99, 35, now.minusDays(8)),
                new Product("Adaptador Ethernet 10Gbps", "Tarjeta de red PCIe con puerto RJ45 de ultra alta velocidad", 89.00, 12, now.minusDays(6)),
                new Product("Cable HDMI 2.1 8K (2m)", "Cable ultra certificado para 8K@60Hz y 4K@120Hz con HDR10+", 19.99, 60, now.minusDays(4)),
                new Product("Regleta Inteligente Surge", "Multicontacto WiFi con 4 tomas y 4 puertos USB programables", 34.50, 28, now.minusDays(2)),
                new Product("SSD Portátil Externo 1TB", "Disco compacto resistente a golpes con transferencia de 1050 MB/s", 99.00, 24, now),
                new Product("Teclado Numérico Wireless", "Teclado mecánico numérico bluetooth recargable", 39.90, 17, now.minusDays(13)),
                new Product("Soporte Auriculares RGB", "Base con puertos USB integrados y tarjeta de sonido 7.1 virtual", 32.00, 22, now.minusDays(11)),
                new Product("Luz LED Barra Monitor", "Barra de iluminación asimétrica antirreflejos para monitor", 55.00, 31, now.minusDays(9)),
                new Product("Docking Station Thunderbolt 4", "Estación de acoplamiento profesional con triple salida de video", 299.00, 7, now.minusDays(7)),
                new Product("Gafas Protección Luz Azul", "Lentes anti fatiga visual para largas sesiones de trabajo y juego", 24.99, 42, now.minusDays(5)),
                new Product("Mando Inalámbrico Pro", "Controlador con palancas de efecto Hall y gatillos ajustables", 69.90, 26, now.minusDays(3)),
                new Product("Pastas Térmicas de Alto Rendimiento", "Compuesto térmico de micropartículas de carbono 4g", 12.50, 80, now.minusDays(1)),
                new Product("Organizador de Cables Magnético", "Set de clips y pasacables para orden de escritorio", 14.99, 70, now),
                new Product("Tablet Gráfica Dibujo Pro 16\"", "Pantalla interactiva 2.5K con lápiz capacitivo de 8192 niveles", 399.00, 9, now.minusDays(15)),
                new Product("Router Mesh WiFi 7 Tri-Band", "Sistema Wi-Fi de alta cobertura hasta 11 Gbps y puertos 2.5G", 320.00, 11, now.minusDays(13)),
                new Product("Caja PC ATX Cristal Templado", "Chasis mid-tower con flujo de aire optimizado y paneles laterales", 115.00, 15, now.minusDays(11)),
                new Product("Ventiladores PC 120mm ARGB (Pack 3)", "Set de ventiladores silenciosos con controlador y mando a distancia", 45.00, 35, now.minusDays(9)),
                new Product("Lector Tarjetas SD UHS-II USB-C", "Lector compacto de aluminio con velocidad hasta 312 MB/s", 27.50, 40, now.minusDays(7)),
                new Product("Estación Carga Inalámbrica 3 en 1", "Cargador MagSafe plegable para smartphone, smartwatch y earbuds", 49.90, 28, now.minusDays(5)),
                new Product("Teclado Ergonómico Split", "Teclado dividido inalámbrico con reposamuñecas acolchado", 119.00, 14, now.minusDays(3)),
                new Product("Proyector Portátil Full HD LED", "Proyector compacto 1080p con altavoz integrado y autofoco", 210.00, 8, now.minusDays(1)),
                new Product("Disco HDD NAS 8TB 7200RPM", "Disco duro para servidores NAS con tecnología CMR y garantía 5 años", 195.00, 16, now.minusDays(14)),
                new Product("Adaptador Bluetooth 5.4 USB", "Receptor dongle nano con alcance hasta 20 metros y baja latencia", 15.99, 65, now.minusDays(10)),
                new Product("KVM Switch Dual DisplayPort", "Conmutador para controlar 2 computadoras con 2 monitores y periféricos", 139.00, 7, now.minusDays(8)),
                new Product("Mochila Impermeable Laptop 17\"", "Mochila antirrobo con puerto USB de carga y compartimento acolchado", 54.90, 32, now.minusDays(6)),
                new Product("Taza Térmica Inteligente App", "Taza de acero inoxidable con control de temperatura persistente", 89.00, 18, now.minusDays(4)),
                new Product("Reposapiés Ergonómico Ajustable", "Soporte de pies con inclinación y textura de masaje para oficina", 38.50, 25, now.minusDays(2)),
                new Product("Brazo Articulado Micrófono", "Soporte de mesa reforzado con canal para ocultar cables XLR/USB", 42.00, 22, now.minusDays(1)),
                new Product("Monitor Portable 15.6\" OLED", "Monitor secundario ultraligero Full HD HDR con conector USB-C", 229.00, 10, now),
                // 30 Productos adicionales diseñados para coincidir en búsquedas por la palabra clave 'Gaming'
                new Product("Auriculares Gaming Wireless 7.1", "Auriculares gaming envolventes con sonido posicional y cancelación de ruido", 149.99, 20, now.minusDays(15)),
                new Product("Consola Gaming Portátil Pro", "Consola gaming de mano con pantalla OLED 120Hz y procesador AMD Z1 Extreme", 699.00, 10, now.minusDays(14)),
                new Product("Silla Gaming Profesional Fabric", "Silla gaming ergonómica con tapizado transpirable de tela de alta densidad", 279.90, 8, now.minusDays(13)),
                new Product("Escritorio Gaming Eléctrico RGB", "Mesa gaming regulable en altura con luces ARGB y soporte para cables", 349.00, 5, now.minusDays(12)),
                new Product("Teclado Gaming 60% Mecánico", "Teclado gaming ultra compacto con switches ópticos e iluminación por tecla", 79.99, 30, now.minusDays(11)),
                new Product("Ratón Gaming Ultra Lightweight", "Ratón gaming de 49 gramos con sensor de 30,000 DPI y cable paracord", 89.90, 25, now.minusDays(10)),
                new Product("Monitor Gaming 240Hz Fast IPS", "Monitor gaming eSports de 27 pulgadas 0.5ms con Nvidia G-Sync Compatible", 399.50, 12, now.minusDays(9)),
                new Product("Gafas Gaming Filtro Azul Pro", "Gafas gaming protectoras anti reflejos con montura ultraligera de titanio", 34.99, 45, now.minusDays(8)),
                new Product("Micrófono Gaming RGB Podcast", "Micrófono gaming USB con botón de silencio táctil y filtro antipop", 69.90, 18, now.minusDays(7)),
                new Product("Altavoces Gaming 2.1 Subwoofer", "Sistema de altavoces gaming con graves potentes e iluminación reactiva", 119.00, 15, now.minusDays(6)),
                new Product("Alfombrilla Gaming Rígida LED", "Alfombrilla gaming de policarbonato con bordes iluminados Chroma", 39.99, 40, now.minusDays(5)),
                new Product("Volante Gaming Force Feedback", "Volante gaming de carreras con pedales ajustables y levas de cambio", 299.99, 7, now.minusDays(4)),
                new Product("Joystick Gaming Vuelo HOTAS", "Combo de palanca de mando gaming y acelerador doble para simulación espacial", 189.00, 9, now.minusDays(3)),
                new Product("Mando Gaming PC / Console BT", "Mando gaming inalámbrico con controles traseros programables y giroscopio", 59.90, 22, now.minusDays(2)),
                new Product("Grip Cinta Gaming para Ratón", "Cinta de agarre antideslizante precortada para ratones gaming", 12.99, 60, now.minusDays(1)),
                new Product("Placa Base Gaming X670E", "Placa madre gaming AM5 con disipadores masivos y WiFi 6E integrado", 350.00, 11, now.minusDays(15)),
                new Product("Memoria RAM Gaming DDR5 RGB", "Memoria RAM gaming 64GB (2x32GB) 6400MHz aluminio cepillado", 220.00, 14, now.minusDays(13)),
                new Product("Caja PC Gaming Doble Cámara", "Chasis gaming de visión panorámica con cristales templados sin pilar", 159.00, 16, now.minusDays(11)),
                new Product("Disipador CPU Gaming RGB", "Torre doble de refrigeración por aire para CPUs gaming de alto TDP", 65.00, 28, now.minusDays(9)),
                new Product("Procesador Gaming Ryzen 7 7800X3D", "El procesador definitivo para gaming con tecnología 3D V-Cache", 419.00, 13, now.minusDays(7)),
                new Product("Tarjeta Gráfica Gaming RTX 4070 Ti", "Placa de video gaming 12GB GDDR6X con ventiladores Tri-Frozr", 799.00, 6, now.minusDays(5)),
                new Product("Router Gaming WiFi 6E Dual", "Router gaming con prioridad de paquetes para juegos y latencia ultrabaja", 250.00, 10, now.minusDays(3)),
                new Product("Soporte Monitor Gaming con Gas", "Brazo neumático para monitores gaming de hasta 34 pulgadas", 69.50, 19, now.minusDays(1)),
                new Product("Panel Acústico Gaming Hexagonal", "Set de 12 paneles de insonorización gaming con diseño geométrico", 44.99, 35, now.minusDays(14)),
                new Product("Tira LED Gaming ARGB Ambient", "Tira LED inteligente para detrás del monitor gaming sincronizable", 29.90, 50, now.minusDays(12)),
                new Product("Capturadora Gaming HDMI 4K", "Dispositivo de captura gaming interna PCIe de 4K@60fps", 179.00, 8, now.minusDays(10)),
                new Product("Auriculares Gaming In-Ear TWS", "Earbuds gaming de baja latencia 40ms con estuche de carga RGB", 49.99, 32, now.minusDays(8)),
                new Product("Refrigerador Móvil Gaming", "Enfriador con célula Peltier para smartphones durante partidas gaming", 25.00, 40, now.minusDays(6)),
                new Product("Cargador Mando Gaming Doble", "Base de carga rápida para 2 mandos gaming con indicación LED", 22.50, 45, now.minusDays(4)),
                new Product("Mochila Gaming Transporte PC", "Mochila para transporte seguro de laptops gaming de 17.3 pulgadas y accesorios", 75.00, 20, now.minusDays(2))
        );

        productRepository.saveAll(products);
    }
}
