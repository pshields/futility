Futility
========

To see it in action (assuming rcssserver and rcssmonitor are installed):

1. Compile the code (this should happen automatically if you've got it in an eclipse Java project with the source directory as `src/` and the build directory as `build/`)
2. Run `rcssserver`
3. Run `rcssmonitor`
4. Run `./manage.sh compete` from the project root directory

Note: Simulating a competition can get pretty CPU-intensive, and the agents currently seem to malfunction when they don't have enough CPU. To test a single agent, run `java -cp build/ futility.Main` from the project root.
