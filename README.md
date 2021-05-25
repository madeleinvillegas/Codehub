# Codehub Social Media Android App
Codehub is a social network android app, where users would be able to interact with one another, and create content. This would greatly benefit newbie programmers, especially students, by providing a community to interact, gather and share knowledge. The app must be user friendly, and easy to use so as to not scare the users. The major features that are to be implemented are: log in, view content, create content, follow, and unfollow. The target audience are new or old programmers, who are enthusiastic about code. The constraint and assumption are that the program will only work on an android device, and  that it would need an internet connection to function as it is powered by a Firebase database. 

# Description
In this application, you can:
* Sign up for free profiles
* View people on the platform
* Follow and Unfollow users on the platform
* Create, Read and Interact with content


The provided code will serve as the social media application for android devices. The functionalities of each activity class (as of 05/25/2021) could be broken down into the following:<br/>
<br/>Activity Classes
* ActivityDisplayFollows - This class allows the user to see their followers and the people they follow as well as other people’s followers/following
* AdminActivity - This class allows the admin to view the reported posts and make a decision whether to keep or delete the reported post
* BaseToolbarActivity - This class allows the app to have a toolbar on the top and bottom of the screen where they can access fragments
* CommentActivity - This class allows users to comment on a post
* EditPostActivity - This class allows the author of a post to edit the post they made
* EditProfileDataActivity - This class allows the user to edit user’s profile data
* ForgotPasswordActivity - This class attempts to contact current user email to reset their password.
* LoginActivity- This class attempts to log in the user based on firebase authentication. 
* RegisterActivity- This class adds new users into the database.
* ReportPostActivity - This class allows users to report posts of other people on the platform that goes beyond the posting guidelines
* SearchActivity - This class allows users to search for other people on the platform
* ViewOtherProfileActivity - This class allows users to view the profiles of other people on the platform where they could follow/unfollow users
* ViewProfileActivity - This class allows users to view their own profile 
* ViewSinglePostActivity - This class allows users to see a single post

Fragment Classes
* CreatePostFragment - The fragment class parses data to create post for the current user
* HomeFragment - This fragment class parses data to provide the newsfeed for the current user 
* NotificationsFragment - This fragment class parses data to provide notifications for the current user
* ProfileTemplate - This class parses data for user xml template of current user and other user. 

Helper Classes
* Comments - This is a template class for parsing comments to Firebase
* FirebaseNotificationsApi - This is a general class to add notifications to notification bar across all activities
* Notifications - This is a template class for parsing notifications to FirebaseRecycleViewAdapter 
* Post - This is a template class for parsing posts to FirebaseRecycleViewAdapter 
* Report - This is a template class for parsing reports to Firebase
* SectionsPagerAdapter- This is a class parses data for the fragment classes. 
* User - This is a template class for parsing users to Firebase
## Getting Started
These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See deployment for notes on how to deploy the project on a live system.


### Prerequisites

What things you need to install the software and how to install them

* Android Studio
* Android Emulator or Device running on a minimum SDK of Android 8.0 (Oreo)

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
