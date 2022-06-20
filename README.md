# Make Meals

## Table of Contents
1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)
2. [Schema](#Schema)

## Overview
### Description
The food app for meal planning  including managing grocery shopping and cooking menus, from scanning grocery shopping receipts to suggesting what type of food you can make with the available ingredients and saving + sharing your favorite recipes 

### App Evaluation
[Evaluation of your app across the following attributes]
- **Category:** Food & Cooking
- **Mobile:** The mobile application is required to do all elements mentioned in the description + managing user accounts
- **Story:** Acts as a one place for all of your food shopping and cooking needs 
- **Market:** Any individuals who like home cooked meals and want a straight way cook anything from what they have, without information overload from a bunch of websites. 
- **Habit:** Users will use this as needed throughout the week
- **Scope:** Will store user grocery information, use APIs to look up possible receipt matches, and allow image processing of receipts to text.

## Product Spec

### 1. User Stories (Required and Optional)

**Required Must-have Stories (P1)**
* 1. Allow users to login/signup with username and password
* 2. Profile page with username and photo + logout button
* 3. Show user currently available ingredients with an option to remove and add ingredients.
* 4. Allows users to scan receipts or make ingredient/grocery entries
* 5. Shows user's favorite recipes (usermarked favorite recipes)
* 6. View Full Recipe Details including cooking instructions
   - Shows the list of ingredients
   - <img width="663" alt="Screen Shot 2022-06-19 at 9 41 39 PM" src="https://user-images.githubusercontent.com/63796975/174526690-8f7d060d-8edc-43c0-9561-789aa8b5d281.png">
* 7. Search Recipes 
    * Using Spoonacular api https://spoonacular.com/food-api/docs
    * Dietary Restriction and intorelances (ex, veg or non)
    * user selects from available ingredients and can also provide additional ingredients
* 8. Share recipe info

**Optional Nice-to-have Stories (p2)**
* 10. A good UI
* 11. Amazon textract to extract receipt data from photo
* 11. Suggest: suggest shopping list based on your saved/favorite recipes and grocery shopping history + **Diet**

**Complicated Stories (p3)**

* 12. Use food image classification APIs

### 2. Screen Archetypes

* Login and Signup Screens
    * Allow users to login/signup with username and password
* Scan Screen
    * Allows users to scan recieps  or make ingredient/grocery entries
* Home Screen
    * Show's user currently available ingredients with an option to remove and add ingredients.
* Recipe Screen
    * View Full Recipe Details including cooking instructions
* Favorites Screen
    * Shows user's favorite recipes (usermarked favorite recipes)
* Search Screen
    * User selects from available ingredients and can also provide additional ingredients
    * Each recipe item in the results list will show
        - title
        - image
        - missedIngredientCount
        - usedIngredientCount 


### 3. Navigation

**Tab Navigation** (Tab to Screen)

* Home Tab
    * Goes to the home screen
* Favorites Tab
    * Goes to the favorites screen
    * Goes to Recipe Screen when a recipe is clicked
* Search Tab
    * Goes to the Search screen
* Scan Tab
    * Goes to the scanning screen

**Flow Navigation** (Screen to Screen)

* Login Screen
   * Signup Screen (back)
   * Home Screen
* Search Screen
    * Results Screen
* Results Screen
    * Recipe Screen
* Favorites Screen
    * Recipe Screen

## Data Models

* User
    * id (primary)
    * username (String)
    * password (String)
    * profilePhoto (File)
    * 
* Recipe (https://spoonacular.com/food-api/docs#Get-Recipe-Information)
    * id (primary)
    * spoonacularId (Number)
    * title (String)
    * image (File)
    * sourceName (String)
    * sourceUrl (String)
    * readyInMinutes (Number)
    * extendedIngredients (Object)
    * vegan (boolean) -- will show a vegan tag
* Grocery (Ingridient)
    * id (primary)
    * name (String)


## Wireframes (in progress ...)

![Wire Frame-1](https://user-images.githubusercontent.com/63796975/174027368-690731c9-1c56-4f86-b35f-850eef1fd378.jpg)

### [BONUS] Digital Wireframes & Mockups

### [BONUS] Interactive Prototype

## Schema 
[This section will be completed in Unit 9]
### Models
[Add table of models]
### Networking
- [Add list of network requests by screen ]
- [Create basic snippets for each Parse network request]
- [OPTIONAL: List endpoints if using existing API such as Yelp]
