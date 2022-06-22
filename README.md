# Make Meals

## Table of Contents
1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)
2. [Schema](#Schema)

## Overview
### Description
The food app for meal planning  including managing grocery shopping and cooking menus, from scanning grocery shopping receipts to suggesting what type of food you can make with the available ingredients and saving + sharing your favorite recipes 

### Timeline

| MU week | Project Week | Focus                                                                                                                                                                                            |
|---------|--------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| 3       | 0            | Write a project plan and discussing with manager                                                                                                                                                 |
| 4       | 1            | Start + finish (project skeleton and unknowns). Midpoint self review due on Thursday - Digital wireframes and mockups - Unknown technologies and SDKs - Initiate Github projects and open issues |
| 5       | 2            | p1: Start (p1 core features  + ambiguous tech probs) - Working code demo for textract or alternative natural language processing tools                                                           |
| 6       | 3            | p1: Continue (p1 core features + ambiguous tech probs)                                                                                                                                           |
| 7       | 4            | p1: Complete (p1 core features + ambiguous tech probs)  p2: Start (p2 stretch features + polish)                                                                                                 |
| 8       | 5            | p2: Continue (additional stretch features + Polish). Final self review due                                                                                                                       |
| 9       | 6            | p3: Delay offset + Finishing touches + improvements                                                                                                                                              |
| 10      | 7            | Complete "Week 10" activities                                                                                                                                                                    |

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
* 3. Show's home page with a list of user saved recipes
* 3. Show user currently available ingredients with an option to remove and add ingredients.
* 4. Allows users to scan receipts or make ingredient/grocery entries
* 5. Shows user's favorite recipes (usermarked favorite recipes)
* 6. View Full Recipe Details including cooking instructions
   - Shows the list of ingredients
* 7. Search Recipes 
    * Using Spoonacular api https://spoonacular.com/food-api/docs
    * Dietary Restriction and intorelances (ex, veg or non)
    * user selects from available ingredients and can also provide additional ingredients 
* 9. Amazon textract to extract receipt data from photo


**Optional Nice-to-have Stories (p2)**
* 8. A good UI
* 9. History: allow authenticated users to view the history of their cooked recipes


**Complicated Stories (p3)**
* 10. Share recipe info:
   - Use image generation sdk (https://dynapictures.com/lp/developers)
   - Use Facebook's 3rd party share APIs
* 11. Suggest: suggest shopping list from a given recipe + **Diet**

### 2. Screen Archetypes

* Login and Signup Screens
    * Allow users to login/signup with username and password
* Scan Screen
    * Allows users to scan recieps  or make ingredient/grocery entries
* Home Screen
    * Show's user's saved recipes
* Ingredients Screen
    * Show's user currently available ingredients with an option to remove and add ingredients.
* Recipe Screen
    * View Full Recipe Details including cooking instructions
      - SECION 0: Shows the favorite and save button
      - SECTION I: pull recipe information with diet, nutrition, and allergen information
      - SECTION II: Shows categorized ingredients details (missed and used) (from get recipe by ingredients endpoint)
      - SECTION III: recipe instructions (Get Analyzed Recipe Instructions endpoint)
    * Saving a recipe will make a `Recipe` entry in our database with the `favorite: false`
    * Favoriting a reciple will also make a `Recipe` entry if it does not exists with `favorite: true` (use the double tap gesture)
* Favorites Screen
    * Shows user's favorite recipes (usermarked favorite recipes)
* Search Screen
    * User selects from available ingredients and can also provide additional ingredients
    * Each recipe item in the results list will show
        - title
        - image
        - calories
        - carbs
        - fat
        - protein
 
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
* Search and Results Screen (Results will be displayed in a recyclerview using the linear layout manager)
* Results Screen
    * Recipe Screen
* Favorites Screen
    * Recipe Screen

## Wireframes
### [BONUS] Digital Wireframes & Mockups
![Screen Shot 2022-06-22 at 3 49 26 PM](https://user-images.githubusercontent.com/63796975/175167214-22c92fc2-c92b-4716-a380-8231fbb4de05.png)

### [BONUS] Interactive Prototype

https://www.figma.com/.../6u95Y8yEE0cTQKWmrO.../MakeMeals...

## Schema 
[This section will be completed in Unit 9]
### Models
[Add table of models]

* User
    * id (primary)
    * username (String)
    * password (String)
    * profilePhoto (File)
* Recipe (https://spoonacular.com/food-api/docs#Get-Recipe-Information)
    * id (primary)
    * spoonacularId (Number) -- will be used to lookup recipe information
    * favorite (Boolean)
* Grocery (Ingridient)
    * id (primary)
    * Date
    * name (String)
   
### Networking
- [Add list of network requests by screen ]
- [Create basic snippets for each Parse network request]
- [OPTIONAL: List endpoints if using existing API such as Yelp]
