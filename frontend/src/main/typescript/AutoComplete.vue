<template>
    <input type="text" :value="value" @input="input($event.target.value)" @keyup.enter="enter">
</template>

<script lang="ts">
export { default } from "./AutoComplete";
</script>

<style src="awesomplete/awesomplete.css"></style>
