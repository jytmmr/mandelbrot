#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <math.h>
#include <time.h>
#include <string.h>

int xpixels = 17500;
int ypixels = 10000;
int currentPixel = 0;		//counter for calculations
int totalPixels;			//the total number of pixels in the image 

double xmin;			// ranges for the calculating the mandelbrot set on
double xmax;			//	real-imaginary plane, where x is real and y
double ymin;			//	is imaginary
double ymax;			//

char outputFile[] = "imagecuda.ppm";		// output file

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
unsigned int convert_to_2d(int x, int y, int scale) {
    return y * scale + x;
}

/*
 *calculate_mandlebrot_set
 *Determines if a given position on the complex plane diverages or not and
 *outputs the results to an  array.
 *INPUTS
 *  *pixelArray - poinjter to array representing output image
 *	xpixels - number of horizontal in output image
 *	ypixels - number or vertical pixels in output image
 *  xmin - smaller cartesian coordinate of the output image in x direction
 *	xmax - larger cartesian coordinate of the output image in x direction
 *  ymin - smaller cartesian coordinate of the output image in y direction
 *	ymax - larger cartesian coordinate of the output image in y direction
 *	
*/
__global__ void calculate_mandlebrot_set( pixel_t *pixelArray, int xpixels, int ypixels, double xmin, double xmax, double ymin, double ymax){
    int MAX_ITERATIONS = 1000;
    double xincrement = (xmax - xmin) / xpixels; // macro to find increments
    double yincrement = (ymax - ymin) / ypixels;

	complex_number_t z;
	z.real = 0;
	z.imag = 0;
	
	complex_number_t c;
	pixel_t *currPixel;

	if (threadIdx.x + blockDim.x * blockIdx.x < xpixels && threadIdx.y + blockDim.y * blockIdx.y < ypixels){
		int myIndex = (threadIdx.x + blockDim.x * blockIdx.x) +  xpixels* (threadIdx.y + blockDim.y * blockIdx.y);
		currPixel = pixelArray + myIndex;
		
		int x = threadIdx.x + blockDim.x * blockIdx.x;             // x posiiton of the pixel
		int y = threadIdx.y + blockDim.y * blockIdx.y;             // y position of the piel
		
		c.real = xmin + x * xincrement;
		c.imag = ymin + y * yincrement;
		
		int toBreak = 0;
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
				toBreak = 1;
				break; //end calculation for current pixel
			}
			i++;
		}
		// never escapes
		if(!toBreak){
			(*currPixel).r = 0;
			(*currPixel).g = 0;
			(*currPixel).b = 0;
		}
	}
}

/*
 *calcMandelbrotCuda
 *A helper function that launches calculate_mandlebrot_set kernel.
 *INPUTS
 *	*data - a pointer to and array of pixel_t for data output
*/
cudaError_t calcMandelbrotCuda(pixel_t *data ){
	cudaError_t cudaStatus;

    // int d_xpixels, d_ypixels;
    // double d_xmin, d_xmax, d_ymin, d_ymax;
	
	
	dim3 threadsPerBlock(32,32,1);
	dim3 numBlocks((xpixels - 1) / threadsPerBlock.x + 1, (ypixels - 1) / threadsPerBlock.y + 1, 1);
	
	cudaStatus = cudaSetDevice(0);
	if (cudaStatus != cudaSuccess) { fprintf(stderr, "cudaSetDevice failed!  Do you have a CUDA-capable GPU installed?"); goto Error; }

    // d_xpixels = xpixels;
    // d_ypixels = ypixels;

    // d_xmin = xmin;
    // d_xmax = xmax;
    // d_ymin = ymin;
    // d_ymax = ymax;

	printf("threads.x = %d, threads.y = %d\n", threadsPerBlock.x, threadsPerBlock.y);
	printf("blocks.x = %d, blocks.y = %d\n", numBlocks.x, numBlocks.y);

	calculate_mandlebrot_set<<<numBlocks, threadsPerBlock>>>(data, xpixels, ypixels, xmin, xmax, ymin, ymax);
	cudaStatus = cudaGetLastError();
	if (cudaStatus != cudaSuccess) { fprintf(stderr, "calculate_mandlebrot_set launch failed: %s\n", cudaGetErrorString(cudaStatus)); goto Error; }
	
	cudaDeviceSynchronize();

    Error:
    return cudaStatus;
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



	//varaibles for timing
    struct timespec start, finish;
    double elapsed;

    printf("Timing Calculations...\n");
    clock_gettime(CLOCK_MONOTONIC, &start);

	


	//do cuda here

    pixel_t *pixelArray = (pixel_t*)(malloc(xpixels*ypixels*sizeof(pixel_t)));  //for cudaMemcpy version
    pixel_t *d_pixelArray;  //for cudaMemcpy version

    // pixel_t *pixelArray;  //for cudaMallocManaged version

	cudaMalloc(&d_pixelArray, totalPixels * sizeof(pixel_t));  //for cudaMemcpy version
	// cudaMallocManaged(&pixelArray, totalPixels * sizeof(pixel_t)); //for culaMallocManaged version

	// cudaError_t cudaStatus = calcMandelbrotCuda(pixelArray);  //for cudaMallocManaged version
    cudaError_t cudaStatus = calcMandelbrotCuda(d_pixelArray); //for cudaMemcpy version

    //calculate elapsed time
    clock_gettime(CLOCK_MONOTONIC, &finish);
    elapsed = (finish.tv_sec - start.tv_sec);
    elapsed += (finish.tv_nsec - start.tv_nsec) / 1000000000.0;
    printf("Calculations took %f seconds\n", elapsed);

    printf("\nTiming cudaMemCpy...\n");  //for cudaMemcpy version
    clock_gettime(CLOCK_MONOTONIC, &start);  //for cudaMemcpy version
    cudaMemcpy(pixelArray, d_pixelArray, totalPixels*sizeof(pixel_t), cudaMemcpyDeviceToHost); //for cudaMemcpy version
    clock_gettime(CLOCK_MONOTONIC, &finish);  //for cudaMemcpy version
    elapsed = (finish.tv_sec - start.tv_sec);  //for cudaMemcpy version
    elapsed += (finish.tv_nsec - start.tv_nsec) / 1000000000.0;  //for cudaMemcpy version
    printf("cudaMemCpy took %f seconds\n", elapsed);  //for cudaMemcpy version


	//start file output timing
    printf("\nStarting file output...\n");
    clock_gettime(CLOCK_MONOTONIC, &start);
	
    fp = fopen(outputFile, "a");
    int x;
    int y;

    for (y = 0; y < ypixels; y++){
        for (x = 0; x < xpixels; x++){
            pixel_t* currPixel;
            currPixel = pixelArray + convert_to_2d(x, y, xpixels);		//get one pixel at a time
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
    

}