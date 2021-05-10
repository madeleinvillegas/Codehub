# Codehub Social Media Android App
Codehub is a social network android app, where users would be able to interact with one another, and share content. This would greatly benefit newbie programmers, especially students, by providing a community to interact, gather and share knowledge. The app must be user friendly, and easy to use so as to not scare the users. The major features that are to be implemented are: log in, share content, create content, follow, and unfollow. The target audience are new or old programmers, who are enthusiastic about code. The constraint and assumption are that the program will only work in an android device, and  that it would need internet connection to function. .

# Description
In this application, you can:
* Sign up for free profiles
* Follow and Unfollow users on the platform
* Read and Share content


The provided code will serve as the social media application for android devices. The functionalities of each activity class (as of 04/19/2021) could be broken down into the following:
```
* CreatePostFragment - The fragment class parses data to create post for current user
* HomeFragment - This fragment class parses data to provide the newsfeed for current user 
* NotificationsFragment - This fragment class parses data to provide notifications for current user
* ProfileTemplate - This class parses data for user xml template of current user and other user. 
* ForgotPasswordActivity - This class attempts to contact current user email to reset their password.
* LoginActivity- This class attempts to log in the user based on the firebase database. 
* RegisterActivity- This class adds new users into the database.
* SectionsPagerAdapter- This class parses data for the fragment classes. 
```
## Getting Started
These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See deployment for notes on how to deploy the project on a live system.


### Prerequisites

What things you need to install the software and how to install them

```
* Android Studio
* Android Emulator or Device running on a minimum SDK of Android 8.0 (Oreo)
```

### Installing

A step by step series of examples that tell you how to get a development env running

1. Make sure that you have complied with the prerequisites section above.
2. Pull the code locally
3. Open Android Studio and select 'Open an existing Android Studio Project'
4. Navigate to the pulled repository.
5. Inside the ‘Codehub” folder. Select settings.gradle file. Gradle would install all the dependencies required (including firebase).
6. Run the code in Android Studio

<b>Additional Information:</b><br/>
[Setting up Android Studio](https://developer.android.com/training/basics/firstapp/running-app)<br/>
[Getting Started with Firebase](https://firebase.google.com/docs/functions/local-emulator)


## Authors

* **Eugene Cedric Reyes** 
* **Felix Gabriel Montanez** 
* **Madelein Villegas** 

## License
[MIT](https://choosealicense.com/licenses/mit/)


