#include <pthread.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <math.h>
#include <time.h>

#define MAX_ITERATIONS 1000

int xpixels = 1750;			//horizontal pixels in output image
int ypixels = 1000;			//veritcal pixels in output image
int currentPixel = 0;		//counter for calculations
int totalPixels;			//the total number of pixels in the image 

double xmin;			// ranges for the calculating the mandelbrot set on
double xmax;			//	real-imaginary plane, where x is real and y
double ymin;			//	is imaginary
double ymax;			//

#define xincrement (xmax - xmin) / xpixels // macro to find increments
#define yincrement (ymax - ymin) / ypixels

pthread_mutex_t theLock;		// a lock that controls access to currentPixel

char outputFile[] = "image.ppm";		// output file

#define NUMTHREADS 4

/*
 * complex_number_t
 * Replresents a complex number by storing separate doubles for real and
 * imaginary parts for the complex number.
*/
typedef struct complex_number_t{
    double real;
    double imag;
}complex_number_t;

/*
 * pixel_t
 * Represents a pixel in the PPM file by storing the RGB components.
*/
typedef struct pixel_t{
    int r;
    int g;
    int b;
} pixel_t;

/*convert_to_2d
 *Finds index in a 1D array given 2D indices
 *INPUTS
 *	x - column position
 *	y - row position
 *	scale - number of elements per row
 *OUTPUT
 *	long index in the 1D array
*/
unsigned long convert_to_2d(int x, int y, int scale) {
    return y * scale + x;
}

/*
 *calculate_mandlebrot_set
 *Determines if a given position on the complex plane diverages or not and
 *outputs the results to a global array.
 *INPUTS
 *	x - horizontal position of pixel
 *	y - veritcal position of pixel
 *  xcoord - real component of complex number
 *	ycoord - imaginary component of complex number
 *	*pixelArray - global array representing output image
*/
void calculate_mandlebrot_set(int x, int y, double xcoord, double ycoord, pixel_t *pixelArray){
    complex_number_t z;
    z.real = 0;
    z.imag = 0;
	
    complex_number_t c;
    c.real = xcoord;
    c.imag = ycoord;

    pixel_t *currPixel;
    currPixel = pixelArray + convert_to_2d(x,y,xpixels); //currPixel points to one pixel in pixelArray

    int i = 0;
    while( i < MAX_ITERATIONS ){

        complex_number_t temp;

        temp.real = z.real;
        temp.imag = z.imag;
        z.real = (z.real * z.real) - (z.imag * z.imag) + c.real; //real
        z.imag = (2*temp.real*temp.imag) + c.imag; // imaginary
        if(z.real*z.real + z.imag*z.imag > 4){
            //save colors to Pixel Array
            (*currPixel).r = (int) 255 * (((1.0*i) / (1.0 * MAX_ITERATIONS)));
            (*currPixel).g = (int) 255 * sqrt(((1.0*i) / (1.0 * MAX_ITERATIONS)));
            (*currPixel).b = (int) 50 * sqrt(1.0 - ((1.0*i) / (1.0 * MAX_ITERATIONS)));

            return; //end calculation for current pixel
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
 *	*data - a pointer to and array of pixel_t for data output
*/
void* mandelbrot_thread(void *data){
    pixel_t* dataArray = (pixel_t*) data;
    while(1) {		//worker thread continually runs
        int myPixel;

        pthread_mutex_lock(&theLock);		//lock the next uncalculated pixel
        myPixel = currentPixel;				//read next uncalculated pixel
        currentPixel += xpixels;			//increment uncalculated pixel
        pthread_mutex_unlock(&theLock);		//unlock uncalculated pixel

        if(myPixel % (totalPixels / 10) == 0 && myPixel != 0)	//some nice user output
            printf("Calculating... %.0f%%  complete.\n", ((100.0 * myPixel) / totalPixels));
        if (myPixel >= totalPixels){		//make sure there are still pixels to calculate
            break;							//if there are no more pixel to calculate, break out of while loop
        }

        int i;
        int endPixel = xpixels + myPixel;		//find the row of pixels to calculate
        for(i = myPixel; i < endPixel; i++){	//for each pixel in the row
            int x = i % xpixels;				// x posiiton of the pixel
            int y = i / xpixels;				// y position of the piel

			// coordinates on the imaginary plane for the pixel
            double ycoord = ymin + y * yincrement;
            double xcoord = xmin + x * xincrement;

			//run the mandelbrot calculation
            calculate_mandlebrot_set(x,y,xcoord,ycoord, dataArray);
        }
    }
    
    //kill the thread
    pthread_exit(0);
}

int main(int argc, char * argv[]){
    if (!(argc == 5 || argc == 2)){
        printf("ERROR: incorrect number of arguments. Run with argument --help for help.\n");
        return 0;
    }
    
    if (argc == 2){
        // char *strHelp = "--help";
        if (!strcmp(argv[1], "--help")){
            printf("USAGE: CompiledMandelbrot [xmin] [xmax] [ymin] [ymax]\n");
        }
        else{
            printf("ERROR: Unrecognized argument. Run with argument --help for help.\n");
        }
        return 0;
    }
	    
	//set range of mandlebrot set from arguments
    xmin = atof(argv[1]);
    xmax = atof(argv[2]);
    ymin = atof(argv[3]);
    ymax = atof(argv[4]);
    
    if (ymin >= ymax){
        printf("ERROR: Invalid range for imaginary axis.\n");
        return 0;
    }
    
    if (xmin >= xmax){
        printf("ERROR: Invalid range for real axis.\n");
        return 0;
    }
    
	//initialize some values
	totalPixels	= xpixels * ypixels;
	
	//initialize output file with PPm header
    FILE *fp;
    fp = fopen(outputFile, "w+");
    fprintf(fp, "P3 \n%d %d \n255\n\n", xpixels, ypixels);
    fclose(fp);
    
    int i;
    pthread_mutex_init(&theLock, NULL);		//initialize mutex
    pixel_t *pixelArray = (pixel_t*)(malloc(xpixels*ypixels*sizeof(pixel_t))); //allocate pixel array
    pthread_t computeThreads[NUMTHREADS];

	//varaibles for timing
    struct timespec start, finish;
    double elapsed;

    printf("Timing Calculations...\n");
    clock_gettime(CLOCK_MONOTONIC, &start);
    
    //start the worker threads
    for(i = 0; i < NUMTHREADS; i++){
        pthread_create(&computeThreads[i], NULL, mandelbrot_thread, (void *) pixelArray);
    }
	
	//join worker threads
    for(i = 0; i < NUMTHREADS; i++){
        pthread_join(computeThreads[i], NULL);
    }
    
    //calculate elapsed time
    clock_gettime(CLOCK_MONOTONIC, &finish);
    elapsed = (finish.tv_sec - start.tv_sec);
    elapsed += (finish.tv_nsec - start.tv_nsec) / 1000000000.0;
    printf("Calculations took %f seconds", elapsed);

	//start file output timing
    printf("\nStarting file output...\n");
    clock_gettime(CLOCK_MONOTONIC, &start);
	
    fp = fopen(outputFile, "a");
    int x;
    int y;
    for (y = 0; y < ypixels; y++){
        for (x = 0; x < xpixels; x++){
            pixel_t* currPixel;
            currPixel = &pixelArray[convert_to_2d(x, y, xpixels)];		//get one pixel at a time
            fprintf(fp, " %d %d %d    ",(*currPixel).r,(*currPixel).g,(*currPixel).b);		//outpit pixel in PPM format
        }
        fprintf(fp, "\n");		//next line in PPM file
    }
    fclose(fp);
    //stop timing
    clock_gettime(CLOCK_MONOTONIC, &finish);
    elapsed = (finish.tv_sec - start.tv_sec);
    elapsed += (finish.tv_nsec - start.tv_nsec) / 1000000000.0;
    printf("File output took %f seconds.\n", elapsed );
    
	//convert the PPM file to a PNG file to save space
    execl("/usr/bin/convert", "/usr/bin/convert", "image.ppm", "image.png", (char *)NULL);
    free(pixelArray);		//free dynamic array

}
