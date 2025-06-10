#!/bin/bash

set -e
set -u

echo "🚀 Starte Server Setup für PiWacheServer..."

# System aktualisieren
echo "🔄 Aktualisiere Systempakete..."
sudo apt update
sudo apt full-upgrade -y

# Apache Webserver installieren
echo "🌐 Installiere Apache Webserver..."
sudo apt install -y apache2

# PHP und PHP-Module installieren
echo "💻 Installiere PHP..."
sudo apt install -y php libapache2-mod-php

# MariaDB und PHP-MySQL installieren
echo "🛢️ Installiere MariaDB und PHP-MySQL..."
sudo apt install -y mariadb-server php-mysql

# MariaDB absichern (halb-automatisiert)
echo "🔒 Starte MariaDB Absicherung. Bitte manuell folgen:"
echo "Switch to unix_socket authentication [Y/n] n"
echo "Change the root password? [Y/n] y"
echo "Remove anonymous users? [Y/n] Y"
echo "Disallow root login remotely? [Y/n] n"
echo "Remove test database and access to it? [Y/n] Y"
echo "Reload privilege tables now? [Y/n] Y"
read -p "👉 Drücke ENTER, um 'sudo mysql_secure_installation' zu starten..."
sudo mysql_secure_installation

# PHPMyAdmin installieren
echo "💻 Installiere PHPMyAdmin..."
sudo apt install -y phpmyadmin

# Symbolischen Link setzen
echo "🔗 Verknüpfe PHPMyAdmin mit Apache..."
sudo ln -s /usr/share/phpmyadmin /var/www/html/phpmyadmin

# Apache neustarten
echo "🔄 Starte Apache Webserver neu..."
sudo systemctl restart apache2

# Hinweis für Datenbankzugriff
echo "🌐 PHPMyAdmin ist erreichbar unter: http://<HostnameServer>.local/phpmyadmin"
echo "👉 Bitte erstelle dort die Datenbank 'piVideos'!"

# Projektpfad definieren
PROJECT_DIR="/home/berry/PiWacheServer"

if [ ! -d "$PROJECT_DIR" ]; then
    echo "❌ Projektverzeichnis $PROJECT_DIR nicht gefunden! Bitte vorher klonen."
    exit 1
fi

# Konfigurationsdatei prüfen
CONFIG_FILE="$PROJECT_DIR/src/main/resources/application.properties"

if [ ! -f "$CONFIG_FILE" ]; then
    echo "⚠️ Konfigurationsdatei $CONFIG_FILE nicht gefunden!"
    echo "Bitte manuell anlegen oder kopieren."
else
    echo "✅ Konfigurationsdatei gefunden: $CONFIG_FILE"
    echo "👉 Bitte die Datei anpassen: nano $CONFIG_FILE"
fi

# SDKMAN installieren
if [ ! -d "$HOME/.sdkman" ]; then
    echo "📦 Installiere SDKMAN..."
    curl -s "https://get.sdkman.io/" | bash
fi

echo "✅ SDKMAN wird initialisiert..."
set +u  # Deaktiviere 'unbound variable' Fehler
source "$HOME/.sdkman/bin/sdkman-init.sh"



# Maven Build
echo "⚙️ Installiere Maven..."
sudo apt install maven

echo "⚙️ Baue Spring Boot Projekt..."
cd "$PROJECT_DIR"
mvn clean package

# systemd Service einrichten
echo "📝 Erstelle systemd Service für Spring Boot..."
sudo bash -c 'cat > /etc/systemd/system/spring.service <<EOF
[Unit]
Description=Spring Boot PiVideos Service
After=network.target

[Service]
User=berry
ExecStart=/usr/bin/java -jar /home/berry/PiWacheServer/target/PiVideos-0.1.1-SNAPSHOT.jar
WorkingDirectory=/home/berry/PiWacheServer/
Restart=always
RestartSec=10
SuccessExitStatus=143
StandardOutput=journal
StandardError=journal

[Install]
WantedBy=multi-user.target
EOF'

# systemd Service aktivieren
echo "🔄 Registriere und starte systemd Service..."
sudo systemctl daemon-reexec
sudo systemctl enable spring.service
sudo systemctl start spring.service

# Service Status anzeigen
sudo systemctl status spring.service

echo "✅ Setup abgeschlossen! Der Spring Boot Service läuft."
