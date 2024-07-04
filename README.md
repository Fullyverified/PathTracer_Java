I'm trying to make an Ascii Pathtracer. Nothing that hasn't been done before :)
Global Illuminiation is here!

![sphere and cube v10 10000 rays random reflections](https://github.com/Fullyverified/ASCII_PathTracer/assets/138776324/65fcac25-cea8-48f5-a6c7-6f25d58adb87)


Current formula: sum from the last bounce to the first bounce :
object brigthness * cosine law / * object reflectivity

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
12. Create a lookup table for values of brightness and their corresponding character: . . , : ; * X # @
13. Change the order of operations so that the first bounce, second bounce, third bounce etc is calculated for a pixel, then move onto the next pixel.
    Instead of doing for the first bounce for each pixel, then moving onto the second bounce etc.
    This is to truly make each pixel an independent entity to enable multi-threading in the future.
14. Completely reorganised the render thread. Method now works for an arbitrary number of bounces.
15. Cubes!

TODO LIST OF THINGS TODO:
1. Add the possibility to output actual pixel values instead of ACSCII
2. Change ray bounce direction to be biased so that specular lighting becomes possible
3. Add colour
4. Multi-threading

Known Bugs:
No specular lighting (whoops)
