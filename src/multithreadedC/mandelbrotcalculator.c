#include <pthread.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <math.h>
#include <time.h>

#define MAX_ITER 1000
#define HALF_ITER MAX_ITER/2.0
#define xpixels  1750
#define ypixels  1000
int currentPixel = 0;
int totalPixels = xpixels * ypixels;
double xmin = -2.5;
double xmax = 1;
double ymin = -1;
double ymax = 1;
#define xincrement  (xmax - xmin) / xpixels
#define yincrement   (ymax - ymin) / ypixels
pthread_mutex_t theLock;
pthread_mutex_t theLockArray;
// char fileName[] = "imagetest.ppm";
char fileName2[] = "pixelqueueimagejeremy2.ppm";

#define NUMTHREADS 1

typedef struct ComplexNumber_t{
    double real;
    double imag;


}ComplexNumber_t;

typedef struct Pixel_t{
    int r;
    int g;
    int b;
} Pixel_t;

unsigned long convertTo2D(int x, int y, int scale) {
    return y * scale + x;
}

int inMandelbrotSet(int x, int y, double xcoord, double ycoord, Pixel_t *pixelArray){
    ComplexNumber_t z;
    z.real = 0;
    z.imag = 0;
    ComplexNumber_t c;
    c.real = xcoord;
    c.imag = ycoord;

    Pixel_t *currPixel;
    currPixel = pixelArray + convertTo2D(x,y,xpixels);

    int i = 0;
    while( i < MAX_ITER ){

        ComplexNumber_t temp;

        temp.real = z.real;
        temp.imag = z.imag;
        z.real = (z.real * z.real) - (z.imag * z.imag) + c.real;
        z.imag = (2*temp.real*temp.imag) + c.imag; //i
        if(z.real*z.real + z.imag*z.imag > 4){
            //save to Pixel Array

            (*currPixel).r = (int) 255 * sqrt(((1.0*i) / (1.0 * MAX_ITER)));
            (*currPixel).g = (int) 255 * sqrt(((1.0*i) / (1.0 * MAX_ITER)));
            (*currPixel).b = (int) 255 * sqrt(((1.0*i) / (1.0 * MAX_ITER)));

            return 0;
        }
        i++;
    }
    // never escapes
    (*currPixel).r = 0;
    (*currPixel).g = 0;
    (*currPixel).b = 0;


}

void* mandelbrot_thread(void *data){
    Pixel_t* dataArray = (Pixel_t*) data;
    while(1) {
        int myPixel;

        pthread_mutex_lock(&theLock);
        myPixel = currentPixel;
        currentPixel += xpixels;
        pthread_mutex_unlock(&theLock);

        if(myPixel % (totalPixels / 10) == 0 && myPixel != 0)
            printf("Calculating... %.0f%%  complete.\n", ((100.0 * myPixel) / totalPixels));
        if (myPixel >= totalPixels){
            break;
        }

        int i;
        int endPixel = xpixels + myPixel;
        for(i = myPixel; i < endPixel; i++){
            int x = i % xpixels;
            int y = i / xpixels;

            // printf("myPixel: %d, x: %d, y: %d\n", myPixel, x, y);

            double ycoord = ymin + y * yincrement;
            double xcoord = xmin + x * xincrement;

            inMandelbrotSet(x,y,xcoord,ycoord, dataArray);
        }




    }

    pthread_exit(0);

}

int main(int argc, char * argv[]){

    xmin = atof(argv[1]);
    xmax = atof(argv[2]);
    ymin = atof(argv[3]);
    ymax = atof(argv[4]);

    FILE *fp2;
    fp2 = fopen(fileName2, "w+");
    fprintf(fp2, "P3 \n%d %d \n255\n\n", xpixels, ypixels);
    fclose(fp2);
    int i;
    pthread_mutex_init(&theLock, NULL);
    Pixel_t *pixelArray = (Pixel_t*)(malloc(xpixels*ypixels*sizeof(Pixel_t)));
    pthread_t computeThreads[NUMTHREADS];


    struct timespec start, finish;
    double elapsed;

    printf("Timing Calculations...\n");
    clock_gettime(CLOCK_MONOTONIC, &start);
    for(i = 0; i < NUMTHREADS; i++){
        pthread_create(&computeThreads[i], NULL, mandelbrot_thread, (void *) pixelArray);
        //printf("I just created thread # %d. Some num is %d.\n", i, some_num);

    }

    for(i = 0; i < NUMTHREADS; i++){
        pthread_join(computeThreads[i], NULL);
    }
    clock_gettime(CLOCK_MONOTONIC, &finish);
    elapsed = (finish.tv_sec - start.tv_sec);
    elapsed += (finish.tv_nsec - start.tv_nsec) / 1000000000.0;
    printf("Calculations took %f seconds", elapsed);

    //pthread_exit(NULL);
    // printf("Before sleep\n");
    // sleep(50);
    // printf("HELLO world.\n");
    //Pixel *pixelArray = (Pixel_t*)(malloc(xpixels*ypixels*sizeof(Pixel_t)));



    // FILE *fp;
    // fp = fopen(fileName, "w+");
    // fprintf(fp, "P3 \n%d %d \n255\n\n", xpixels, ypixels);
    // fclose(fp);






    // double y = ymax;
    // for(y; y >= ymin; y = y-yincrement){






    printf("\nStarting file output...\n");
    clock_gettime(CLOCK_MONOTONIC, &start);

    fp2 = fopen(fileName2, "a");
    int x;
    int y;
    for (y = 0; y < ypixels; y++){
        for (x = 0; x < xpixels; x++){
            Pixel_t* currPixel;
            currPixel = &pixelArray[convertTo2D(x, y, xpixels)];
            fprintf(fp2, " %d %d %d    ",(*currPixel).r,(*currPixel).g,(*currPixel).b);
        }
        fprintf(fp2, "\n");
    }
    clock_gettime(CLOCK_MONOTONIC, &finish);
    elapsed = (finish.tv_sec - start.tv_sec);
    elapsed += (finish.tv_nsec - start.tv_nsec) / 1000000000.0;
    printf("File output took %f seconds.\n", elapsed );

    execl("/usr/bin/commit", "/usr/bin/commit", "image.ppm", "image.png", (char *)NULL);
    free(pixelArray);

}
