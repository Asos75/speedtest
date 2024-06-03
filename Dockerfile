# Uporabi uradno sliko Ubuntu kot osnovno sliko
FROM ubuntu:latest

# Namesti Nginx strežnik
RUN apt-get update && apt-get install -y nginx

# Določi, kateri port bo dostopen za HTTP zahtevke
EXPOSE 80

# Nastavi Nginx kot storitev, ki se bo zagnala ob zagonu kontejnerja
CMD ["nginx", "-g", "daemon off;"]
