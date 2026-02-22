FROM nginx:1.25-alpine

# Remove default nginx site
RUN rm -rf /usr/share/nginx/html/*

# Copy AngularJS app
COPY index.html    /usr/share/nginx/html/
COPY styles.css    /usr/share/nginx/html/
COPY app/          /usr/share/nginx/html/app/

# Copy nginx config
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
