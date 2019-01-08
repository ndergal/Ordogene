# ORDOGENE #


Ordogene is a software that can be used to determine, using a genetic algorithm, the actions of a given process to maximize or minimize some resources in a minimum amount of time. These processes can be very different, such as getting ready in the morning, organizing an event or manufacturing industrial parts.  
  
The software provides, after calculation, a scheduling taking into account the parameters of the user and the constraints of the process.  
  
**To use it well, it requires a good understanding of the terms, concepts and methods used in the user manual.**
  
**It is strongly advised to study it in detail.**  

## Dependencies

* Maven 3.5.2  
  
* Java 8  

## Deployment instructions

### How to run the project  

To install the project you have to modify the three configurations files **ordogene.conf.json** in :  

    ordogene-parent/ordogene-file/src/test/resources
    ordogene-parent/ordogene-api/src/test/resources
    ordogene-parent/ordogene-algorithme/src/test/resources
    
After that run the following command in the **ordogene-parent** folder :  

    mvn install
    
### How to run tests

Inside the **ordogene-parent** directory run :  

    mvn verify or mvn test
    
### Limitation

* Difficulties to modelize recurring tasks

* Difficulties to modelize zero-time tasks

* Difficulties to modelize an action with an OR in input.  

### Possible Evolutions

* adding the mutation feature (see the Interface **Alterer** page 41 of the Jenetics LIBRARY USER'S MANUAL and the **Mutator** class of Jenetics' source code).  

### Documentation ###

[User Manual](https://bitbucket.org/darwinners/ordogene/src/b32ed07f152c29d3e0c05ca8e07c08b5772017c1/ordogene-parent/docs/User_Manual.pdf?at=master "link to the User Manual")

[Jenetics LIBRARY USER'S MANUAL](http://jenetics.io/manual/manual-4.0.0.pdf "link to the Jenetics LIBRARY USER'S MANUAL")  

### Contribution guidelines ###

* If you want to contribute to the project you have to fork.
