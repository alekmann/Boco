<template>
  <div id="map-background-select">
    <div id="search-box-container">
      <input
        id="pac-input"
        class="controls"
        type="text"
        placeholder="Søk på addresse"
      />
    </div>

    <div id="map-container-select">
      <div
        id="map"
        :style="'width:' + this.width + ';height:' + this.height"
      ></div>
    </div>
    <div id="selected-position" v-if="locationObject">
      Valgt addresse:
      {{
        locationObject.address +
        ", " +
        locationObject.postCode +
        " " +
        locationObject.city +
        ", " +
        locationObject.country
      }}
    </div>
    <div id="finish-button">
      <button class="input button" @click="toggleMap">Ferdig</button>
    </div>
  </div>
</template>

<script>
import { Loader } from "@googlemaps/js-api-loader";

export default {
  props: ["showPositionOnly"],
  data() {
    return {
      locationObject: null,
      height: "500px",
      width: "800px",
    };
  },
  methods: {
    toggleMap() {
      this.$emit("hideMap", this.locationObject);
    },
  },
  async created() {
    const loader = new Loader({
      apiKey: "AIzaSyDge7nkZbCSQ57dNs6xgDW0is1-XYVhzOI",
      libraries: ["places"],
    });

    const mapOptions = {
      center: {
        lat: 63.429362,
        lng: 10.394357,
      },
      zoom: 12,
    };

    let google = await loader.load();
    var map = new google.maps.Map(document.getElementById("map"), mapOptions);
    // use this to access this instance inside google maps
    const that = this;
    var marker = null;
    const setMarker = (location) => {
      if (marker == null) {
        marker = new google.maps.Marker({
          position: location,
          map: map,
        });
        marker.setMap(map);
      } else {
        marker.setMap(null);
        marker = new google.maps.Marker({
          position: location,
          map: map,
        });
      }
    };

    // SEARCHBOX
    // Create the search box and link it to the UI element.
    const input = document.getElementById("pac-input");
    const searchBox = new google.maps.places.SearchBox(input);

    map.controls[google.maps.ControlPosition.TOP_LEFT].push(input);

    // Bias the SearchBox results towards current map's viewport.
    map.addListener("bounds_changed", () => {
      searchBox.setBounds(map.getBounds());
    });

    // Listen for the event fired when the user selects a prediction and retrieve
    // more details for that place.
    searchBox.addListener("places_changed", () => {
      const places = searchBox.getPlaces();

      var geocoder = new google.maps.Geocoder();
      geocoder.geocode(
        {
          latLng: places[0].geometry.location,
        },
        (results, status) => {
          if (status == google.maps.GeocoderStatus.OK) {
            if (results[0]) {
              let res = results[0].address_components;

              let address1 = "";
              let address2 = "";
              let postcode = "";
              let city = "";
              let country = "";

              for (let i = 0; i < res.length; i++) {
                if (res[i].types[0] == "street_number")
                  address1 = res[i].long_name;
                if (res[i].types[0] == "route") address2 = res[i].long_name;
                if (res[i].types[0] == "postal_code")
                  postcode = res[i].long_name;
                if (res[i].types[0] == "postal_town") city = res[i].long_name;
                if (res[i].types[0] == "country") country = res[i].long_name;
              }

              let locationObject = {
                address: address2 + " " + address1,
                postCode: postcode,
                city: city,
                country: country,
                latitude: places[0].geometry.location.lat(),
                longitude: places[0].geometry.location.lng(),
              };

              if (res[0].long_name != null) {
                map.setCenter({
                  lat: places[0].geometry.location.lat(),
                  lng: places[0].geometry.location.lng(),
                });
                map.setZoom(16);
                that.locationObject = locationObject;

                // add marker
                setMarker(places[0].geometry.location);
              } else {
                that.$notify({
                  type: "error",
                  title: "En feil oppstod",
                  info: "Stedet må være en full addresse",
                });
              }
            }
          }
        }
      );
    });

    // CLICK MAP
    map.addListener("click", (event) => {
      var geocoder = new google.maps.Geocoder();
      geocoder.geocode(
        {
          latLng: event.latLng,
        },
        (results, status) => {
          if (status == google.maps.GeocoderStatus.OK) {
            if (results[0]) {
              let res = results[0].address_components;

              let address1 = "";
              let address2 = "";
              let postcode = "";
              let city = "";
              let country = "";

              for (let i = 0; i < res.length; i++) {
                if (res[i].types[0] == "street_number")
                  address1 = res[i].long_name;
                if (res[i].types[0] == "route") address2 = res[i].long_name;
                if (res[i].types[0] == "postal_code")
                  postcode = res[i].long_name;
                if (res[i].types[0] == "postal_town") city = res[i].long_name;
                if (res[i].types[0] == "country") country = res[i].long_name;
              }

              let locationObject = {
                address: address2 + " " + address1,
                postCode: postcode,
                city: city,
                country: country,
                latitude: event.latLng.lat(),
                longitude: event.latLng.lng(),
              };
              that.locationObject = locationObject;

              // add marker
              setMarker(event.latLng);
            }
          }
        }
      );
    });
  },
};
</script>

<style>
#map-background-select {
  background-color: white;
  border-radius: 10px;
  width: 100%;
  height: 100%;
  display: grid;
  grid-template-rows: auto auto auto;
  z-index: 12;
}

#map-container-select {
  grid-row: 1/2;
  justify-self: center;
  padding-top: 50px;
  background-color: white;
}

#finish-button {
  grid-row: 3/4;
  width: 300px;
  height: 50px;
  justify-self: center;
}

#selected-position {
  grid-row: 2/3;
  justify-self: center;
}

#pac-input {
  width: 400px;
}
</style>
