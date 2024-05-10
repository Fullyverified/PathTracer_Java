I'm trying to make an Ascii Pathtracer. Nothing that hasn't been done before :)

![spher erender](https://github.com/Fullyverified/ASCII_RayTracer/assets/138776324/553ab64d-12ce-4391-a3cf-c72657531b53)

Sphere at 6, 0, 0 with radius 1. Second sphere at 12, 0, -5 with radius 1.25. Light 6, 0.5, 5 radius 1.

The current method for calculating light is to simply sum up the light per pixel for each bounce.
It does create shadows but its very approximate and frankly an inaccurate and inefficient way of doing it.
I will try to implement a proper light transport solution soon.

Done:
1. Create ray and sphere objects.
2. Give the ray a positon and a direction vector.
3. Normalise the direction vector.
4. Discard the sphere if the Ray has no intersection points.
5. If the ray will intersect, march the ray until it intersects.
6. Add an interface that runs intersectionCheck() for each object added to the scene.
7. End the ray if it finds an intersection
8. Store the intersection location as part of the ray object.
9. Create more than one ray, such that each ray is a pixel of the camera.
10. Print (render) the final output :)
11. Spawn a new ray at the intersection towards the direction of each lightsource to test for shadows
12. Create a lookup table for values of brightess and their corresponding character, e.g. . , : ; / % # @

TODO LIST OF THINGS TODO:
1. Implement a correct monte carlo approximation
2. Change the order of operations so that the first bounce, second bounce, third bounce etc is calculated for a pixel, then move onto the next pixel.
    Instead of doing for the first bounce for each pixel, then moving onto the second bounce etc.
    This is to truly make each pixel an independent entity to enable mutli-threading in the future.
3. Add more shapes, cubes, flat planes, and the aribtuary shapes.
4. Add colour
5. Try to make it run in real time and add controls so it can be game-ified (?)
6. Re-write it in C++ (in order to learn C++)
7. Re-write it in Vulkan to make it run on the GPU (big maybe) 

Known Bugs:
