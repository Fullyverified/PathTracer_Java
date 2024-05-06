I'm trying to make an Ascii Pathtracer. Nothing that hasn't been done before :)

![spher erender](https://github.com/Fullyverified/ASCII_RayTracer/assets/138776324/553ab64d-12ce-4391-a3cf-c72657531b53)
sphere1 (6, 0, 0) radius 1
sphere 2 (12, 0, -5) radius 1.25
Light (6, 0.5, 5) radius 1

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

TODO:

1. Create a lookup table for values of brightess and their corresponding character, e.g. . , : ; / % # @
2. Spawn a new ray at the intersection towards the direction of each lightsource to test for shadows
3. Implemenet path tracing (?)

Known Bugs:
