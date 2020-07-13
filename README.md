# Orbit

## Table of Contents
1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)
2. [Schema](#Schema)

## Overview
### Description
[Description of your app]
Proximity Based social networking platform, where people can ask questions and exchange goods and services based on proximity

### App Evaluation

- **Category:** Marketplace
- **Mobile:** Location, Maps, payments,
- **Story:** Asking questions and for items of people nearby
- **Market:** Students and professionals in closed spaces 
- **Habit:** looking for interesting leads or questions


## Product Spec

### 1. User Stories (Required and Optional)

* Users can make an account
* Users can login if they have an exsisting account
* Users can send messages that only send to users in a caertain radius
* Users Messages are sorted in to types inquires, items, trades, revision help, study budy request
* History page of previous leads followed



**Optional Nice-to-have Stories**

* Users can exchange money for goods and services
* UI is appealing 
* Users can see other users on a map
* Push notification when there is a post around around users
* Users can sort by message types


### 2. Screen Archetypes

* Login
    * Users can login if they have an exsisting account
* Register
    * Users can make an account
* Home/stream
    * Users can send messages that only broadcast to users in a certain radius
    * Users Messages have  types inquires, items, trades, revision help, study budy request
* Individual item selected
   * Users can add comments to individual posts
* Map
   * Users can see comments

   

### 3. Navigation

**Tab Navigation** (Tab to Screen)

* Home
* All
* Send meesage
* Profile
* Map

**Flow Navigation** (Screen to Screen)

* Login
    * home
    * register 
   
* Register
    * login

* Home/stream
    * Send meesage
    * login
    
* Individual item selected
    * home 
    * Send meesage
    
* Send Meesage 
   * home 
   * profile




## Wireframes
<img src="https://github.com/prycey/orbit/blob/master/orbit.jpg" width=600>



## Schema 
### Models
#### Messages

   | Property      | Type     | Description |
   | ------------- | -------- | ------------|
   | objectId      | String   | unique id for the user post (default field) |
   | author        | Pointer to User| image author |
   | message       | String   | image caption by author |
   | commentsCount | Number   | number of comments that has been posted to an image |
   | createdAt     | DateTime | date when post is created (default field) |
   | updatedAt     | DateTime | date when post is last updated (default field) |
   
#### Comments 
 | Property      | Type     | Description |
   | ------------- | -------- | ------------|
   | objectId      | String   | unique id for the user post (default field) |
   | message       | Pointer to message| image author |
   | commentobj       | Pointer to comment| image author |
   | comment       | String   | image caption by author |
   | commentsCount | Number   | number of comments that has been posted to an image |
   | createdAt     | DateTime | date when post is created (default field) |
   | updatedAt     | DateTime | date when post is last updated (default field) |
#### User
####
  
### Networking
#### List of network requests by screen
   - Home Feed Screen
      - (Read/GET) Query all posts where user is author
         let query = PFQuery(className:"Post")
         query.whereKey("author", equalTo: currentUser)
         query.order(byDescending: "createdAt")
         query.findObjectsInBackground { (posts: [PFObject]?, error: Error?) in
            if let error = error { 
               print(error.localizedDescription)
            } else if let posts = posts {
               print("Successfully retrieved \(posts.count) posts.")
           // TODO: Do something with posts...
            }
         }
         ```
      - (Create/POST) Create a new like on a post
      - (Delete) Delete existing like
      - (Create/POST) Create a new comment on a post
      - (Delete) Delete existing comment
   - Create messsage Screen
      - (Create/POST) Create a new message object
   - Profile Screen
      - (Read/GET) Query logged in user object
      - (Update/PUT) Update user profile image
