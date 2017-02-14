#include <pthread.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <math.h>
#include <time.h>

#define MAX_ITERATIONS 1000

int xpixels = 1750;			//horizontal pixels in output image TODO - SET FROM FROM COMMAND LINE
int ypixels = 1000;			//veritcal pixels in output image TODO - SET FROM FROM COMMAND LINE
int currentPixel = 0;		//counter for calculations
int totalPixels;			//the total number of pixels in the image 

double xmin;			// ranges for the calculating the mandelbrot set on
double xmax;			//	real-imaginary plane, where x is real and y
double ymin;			//	is imaginary
double ymax;			//

#define xincrement  (xmax - xmin) / xpixels
#define yincrement   (ymax - ymin) / ypixels

pthread_mutex_t theLock;		// a lock that controls access to currentPixel

char outputFile[] = "image.ppm";		// output file

#define NUMTHREADS 1

/*
 * ComplexNumber_t
 * Replresents a complex number by storing separate doubles for real and
 * imaginary parts for the complex number.
*/
typedef struct ComplexNumber_t{
    double real;
    double imag;
}ComplexNumber_t;

/*
 * Pixel_t
 * Represents a pixel in the PPM file by storing the RGB components.
*/
typedef struct Pixel_t{
    int r;
    int g;
    int b;
} Pixel_t;

/*convertTo2D
 *Finds index in a 1D array given 2D indices
 *INPUTS
 *	x - column position
 *	y - row position
 *	scale - number of elements per row
 *OUTPUT
 *	long index in the 1D array
*/
unsigned long convertTo2D(int x, int y, int scale) {
    return y * scale + x;
}

/*
 *inMandlebrotSet
 *Determines if a given position on the complex plane diverages or not and
 *outputs the results to a global array.
 *INPUTS
 *	x - horizontal position of pixel
 *	y - veritcal position of pixel
 *  xcoord - real component of complex number
 *	ycoord - imaginary component of complex number
 *	*pixelArray - global array representing output image
*/
void inMandelbrotSet(int x, int y, double xcoord, double ycoord, Pixel_t *pixelArray){
    ComplexNumber_t z;
    z.real = 0;
    z.imag = 0;
	
    ComplexNumber_t c;
    c.real = xcoord;
    c.imag = ycoord;

    Pixel_t *currPixel;
    currPixel = pixelArray + convertTo2D(x,y,xpixels);

    int i = 0;
    while( i < MAX_ITERATIONS ){

        ComplexNumber_t temp;

        temp.real = z.real;
        temp.imag = z.imag;
        z.real = (z.real * z.real) - (z.imag * z.imag) + c.real;
        z.imag = (2*temp.real*temp.imag) + c.imag; //i
        if(z.real*z.real + z.imag*z.imag > 4){
            //save to Pixel Array
            (*currPixel).r = (int) 255 * sqrt(((1.0*i) / (1.0 * MAX_ITERATIONS)));
            (*currPixel).g = (int) 255 * sqrt(((1.0*i) / (1.0 * MAX_ITERATIONS)));
            (*currPixel).b = (int) 255 * sqrt(((1.0*i) / (1.0 * MAX_ITERATIONS)));

            return;
        }
        i++;
    }
    // never escapes
    (*currPixel).r = 0;
    (*currPixel).g = 0;
    (*currPixel).b = 0;


}

/*
 *mandlebrot_thread
 *A worker thread that calculates if individual pixels are in the Mandelbrot set.
 *INPUTS
 *	*data - a pointer to and array of Pixel_t for data output
*/
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

	//initialize some values
	totalPixels	= xpixels * ypixels;
	 
    FILE *fp;
    fp = fopen(outputFile, "w+");
    fprintf(fp, "P3 \n%d %d \n255\n\n", xpixels, ypixels);
    fclose(fp);
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
    }

    for(i = 0; i < NUMTHREADS; i++){
        pthread_join(computeThreads[i], NULL);
    }
    clock_gettime(CLOCK_MONOTONIC, &finish);
    elapsed = (finish.tv_sec - start.tv_sec);
    elapsed += (finish.tv_nsec - start.tv_nsec) / 1000000000.0;
    printf("Calculations took %f seconds", elapsed);


    printf("\nStarting file output...\n");
    clock_gettime(CLOCK_MONOTONIC, &start);

    fp2 = fopen(outputFile, "a");
    int x;
    int y;
    for (y = 0; y < ypixels; y++){
        for (x = 0; x < xpixels; x++){
            Pixel_t* currPixel;
            currPixel = &pixelArray[convertTo2D(x, y, xpixels)];
            fprintf(fp, " %d %d %d    ",(*currPixel).r,(*currPixel).g,(*currPixel).b);
        }
        fprintf(fp, "\n");
    }
    clock_gettime(CLOCK_MONOTONIC, &finish);
    elapsed = (finish.tv_sec - start.tv_sec);
    elapsed += (finish.tv_nsec - start.tv_nsec) / 1000000000.0;
    printf("File output took %f seconds.\n", elapsed );

    execl("/usr/bin/convert", "/usr/bin/convert", "image.ppm", "image.png", (char *)NULL);
    free(pixelArray);

}
