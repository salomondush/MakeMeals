# Make Meals

## Table of Contents
1. [Overview](#Overview)
2. [Product Spec](#Product-Spec)
3. [Wireframes](#Wireframes)
4. [Schema](#Schema)
5. [Components and connections diagram](#Components-and-connections-diagram)

## Overview
### Description
The food app for meal planning  including managing grocery shopping and cooking menus, from scanning grocery shopping receipts to suggesting what type of food you can make with the available ingredients and saving + sharing your favorite recipes 

### Timeline

| MU week | Project Week | Focus                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               |
|---------|--------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| 3       | 0            | <ul> <li>- [x] Write a project plan and discussing with manager </li> </ul>                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         |
| 4       | 1            | <ul> <li> p1: start core features + project skeleton and unknowns + midpoint self review </li> <li>- [x] Digital wireframes and mockups </li> <li>- [x] Figure out unknown technologies and SDKs (Amazon Textract) </li> <li>- [x] Initiate Github projects and open issues </li> <li>- [x] User login and signup </li> <li>- [x] Basic Navigation between the main screens </li> <li>- [x] Write a node.js textract server to parse images </li> <li>- [x] Ingredients page + ingredients manual entry </li> <li>- [x] Spoonacular API account setup and making example search queries </li> </ul> |
| 5       | 2            | <ul> <li> p1: Continue(p1 core features  + ambiguous tech probs) </li> <li>- [x] Search Page </li> <li>- [x] Home Page </li> <li>- [x] Profile Page </li> <li>- [x] Find out how to use facebook sharing </li> <li>- [x] Integrate textract to scan recipes  </li> <li>- [x] favorites Page </li> <li>- [x] Adding progress bar to all network request/query pages </li> </ul>                                                                                                                                                                                                                                                                                                                                                    |
| 6       | 3            | <ul> <li> p1: Continue (p1 core features + ambiguous tech probs) </li> <li>- [x] Recipe details page: show Recipe title, nutrition facts, ingredients with images, cooking instructions, and recipe image </li><li>- [x] Allow user to favorite/unfavorite + save/unsave recipe from the recipe details page </li><li>- [x] Add image buttons for user to generate shopping list from recipe or share the recipe </li><li>- [x] Add recipe sharing feature </li><li>- [x] generate shopping list from recipe </li> <li>- [x] allow user's to save and view shopping lists </li><li>- [x] allow user's to customize shopping list from recipe with the swiping to delete gesture </li><li>- [x] Allow user's to undo item deletes when adding to shopping list </li><li>- [x] Visually showing strikethroughs and counters for checked/unchecked shopping items in the user's cart page </li>  </ul>                                                                                                                                                                                                                                                                                                                  |
|         | 4            | <ul> <li> p1: Complete (p1 core features + ambiguous tech probs) </li> <li> p2: Start (p2 stretch features + polish) <li>- [x] Implement the recipe sharing page that allows user's to customize the recipe poster (zoom + resize + overlay recipe components, such as the recipe image, nutrition facts, cooking instructions, and ingredients with images) </li> </li> <li>- [x] Implement a connectivity manager with a callback listener for the devices network status to notify the user when the device is offline/online  </li> <li>- [x] double tap gesture to favorite/unfavorite recipe and updating the adapter according to current page, i.e if no favorites page, then remove the recipe, but keep it fi on search results page or home page </li><li>- [x] Added cacheing to all parse reuqests to make the application faster in slow/no network conditions </li><li>- [x] Demo + Testing + fixing resulting bugs </li> </ul>                                                                                                                                                                                                                                                                                                                                                    |
| 8       | 5            | <ul> <li>p2: Continue (additional stretch features + Polish). Final self review due</li> <li>- [ ] Enhancing UI </li> <li>- [ ] better icons </li> <li>- [x] better color and texture combinations (non-blurry icons - VSGs) </li> <li>- [x] simplified interface consistent across all screens </li> <li>- [x] Further search Parameters for recipes </li><li>- [x] Refactor saved and favorite recipes to one fragment with switch filtering selecters to display either or both </li><li>- [x] **Third complex: **Home page to display recipe recommendations depending on user's frequently saved/unsaved favorited/unfavorite recipe actions and the time of the day </li> <li>- [x] Refacor all data fragments to use the ViewModel framework to manage and retain UI related data to avoid unnessessary expensive network calls when switching between fragments </li> <li>- [x] Caching user search history using android's room framework on a local db </li> </ul>                                                                                                                                                                          |
| 9       | 6            | p3: Delay offset + Finishing touches + improvements                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 |
| 10      | 7            | Complete "Week 10" activities                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       |

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
![Screen Shot 2022-06-22 at 3 49 26 PM](https://user-images.githubusercontent.com/63796975/174027368-690731c9-1c56-4f86-b35f-850eef1fd378.jpg)
### [BONUS] Digital Wireframes & Mockups
https://www.figma.com/.../6u95Y8yEE0cTQKWmrO.../MakeMeals...
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
    * shoppingList (Array)
* Recipe (https://spoonacular.com/food-api/docs#Get-Recipe-Information)
    * id (objectId)
    * user (User objectId reference)
    * updatedAt (Date)
    * createdAt (Date)
    * title (String)
    * imageUrl (String)
    * spoonacularId (Number) -- will be used to lookup recipe information
    * favorite (Boolean)
    * saved (Boolean)
    * diets (Array)
    * dishTypes (Array)
    * servings (Number)
    * extendedIngredients (Array)
    * analyzedInstructions (Array)
    * readyInMinutes (Number)
    * nutrients (Array)
* Grocery (Ingridient)
    * id (objectId)
    * user (User objectId reference)
    * updatedAt (Date)
    * createdAt (Date)
    * name (String)
* ShoppingItem
   * fields (Object)
   * isChecked (Boolean)
* Recommendation
   * OjectId
   * updateAt
   * createdAt
   * user (pointer reference to user class)
   * diets
   * cuisines
   
### Components and connections diagram
![Screen Shot 2022-07-22 at 3 35 30 PM](https://user-images.githubusercontent.com/63796975/180577150-e57e64fc-cf4b-4c4c-8d6a-0d3c4dc0f4b1.png)

