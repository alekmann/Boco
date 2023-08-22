# IDATT2106 Team 10 Frontend

### Product description
BoCo (Borrow Comunity) is a webapp made for private and corporate stakeholders who want to borrow or rent out products. The users can publish posts with items they want to rent out, or borrow items published by others.

### Team members
- Aleksander Brekke Røed
- Moaaz Bassam Yanes
- Nicolay Caspersen Roness
- Leonard Opsal Taklo
- Kim Johnstuen Rokling
- Erlend Rønning
- Markus Solli Pedersen
- Nicolai Thorer Sivesind
- Maamoun Adnan Hmaidoush

### Frontend architecture
The frontend is made with Vue3 and various libraries

**Libraries**
This project makes use of some great external libraries: 

- For api requests, we use [axios](https://www.npmjs.com/package/axios)
- For icons, we use [FontAwesome](https://fontawesome.com/)
- For token decoding in the frontend, we use [jwt-decode](https://www.npmjs.com/package/jwt-decode)
- For mapping the locations, we use [Google Maps API](https://developers.google.com/maps)
- For notifications, we use [Vue3-Notifications](https://www.npmjs.com/package/@kyvg/vue3-notification)
- For calenders, we use [V-calendar](https://vcalendar.io/)
- For state, we use [Vuex Persisted State](https://www.npmjs.com/package/vuex-persistedstate)
- For slideshow, we use [Vue3-carousel](https://ismail9k.github.io/vue3-carousel/)
- For image preview, we user [Vue easy lightbox](https://www.npmjs.com/package/vue-easy-lightbox)
- For styling notifications, we use [Sass and Sass loader](https://www.npmjs.com/package/sass-loader)
- For loading google maps, we use [Js Api Loader](https://www.npmjs.com/package/@googlemaps/js-api-loader)
- For loading animations, we use [Vue element loading](https://www.npmjs.com/package/vue-element-loading)

### Installation tutorial
Prerequisites for insalling and running the frontend:
- Git, download [here](https://git-scm.com/downloads)
- NodeJs, download [here](https://nodejs.org/en/download/)


Clone the project by running this line:
```console
 $ git clone https://gitlab.stud.idi.ntnu.no/idatt2106-2022-10/frontend.git
   ``` 
This will clone the project into a folder in your working directory.
   
After you have cloned the project, open the terminal in root of the project and run this command:
```console
    $ npm install
```
After the modules are installed, you can run the application with the command:
```console
    $ npm run serve
```
When the [backend](https://gitlab.stud.idi.ntnu.no/idatt2106-2022-10/backend) and frontend is up and running, you can fill the database with test data. Visit the link:
```console
    http://localhost:8080/fill
```
After the database has been filled with testdata you can log in with the user: user@user.com with password 123
