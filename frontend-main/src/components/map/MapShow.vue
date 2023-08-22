<template>
  <div id="show-map-background">
    <div id="show-map-container">
      <div
        id="map"
        :style="'width:' + this.width + ';height:' + this.height"
      ></div>
    </div>
    <div id="show-selected-position" v-if="location">
      {{
        location.address +
        ", " +
        location.postCode +
        " " +
        location.city +
        ", " +
        location.country
      }}
    </div>
  </div>
</template>

<script>
import { Loader } from "@googlemaps/js-api-loader";

export default {
  props: ["locationObject", "width", "height"],
  data() {
    return {
      location: null,
    };
  },
  watch: {
    async locationObject(oldVal, newVal) {
      this.location = this.locationObject;

      const loader = new Loader({
        apiKey: "AIzaSyDge7nkZbCSQ57dNs6xgDW0is1-XYVhzOI",
        libraries: ["places"],
      });

      const myLatLng = {
        lat: this.location.latitude,
        lng: this.location.longitude,
      };
      const mapOptions = {
        center: myLatLng,
        zoom: 12,
      };

      let google = await loader.load();
      var map = new google.maps.Map(document.getElementById("map"), mapOptions);
      var marker = new google.maps.Marker({
        position: myLatLng,
        map: map,
      });
      marker.setMap(map);
    },
  },
};
</script>

<style>
#show-map-background {
  display: grid;
  grid-template-rows: auto auto;
}

#show-map-container {
  grid-row: 1/2;
  justify-self: center;
  overflow: hidden;
  border-radius: var(--standard-border-radius);
}

#show-selected-position {
  width: 100%;
  grid-row: 2/3;
  justify-self: center;
  font-size: 14px;
  padding: 10px 0px;
}
</style>
