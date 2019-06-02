FROM node:alpine	
RUN apk add --no-cache git
RUN apk add --no-cache openssh
WORKDIR /app
RUN git clone https://github.com/teodossidossev/ipb-platform.git /app
RUN npm install
EXPOSE 8080:8080
CMD ["npm", "start"]