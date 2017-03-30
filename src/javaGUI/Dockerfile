FROM jytmmr/myapp:5.0

RUN git clone https://github.com/jytmmr/mandelbrot.git
RUN ls -a
WORKDIR /mandelbrot/src/javaGUI
RUN chmod 777 run
RUN ./run
