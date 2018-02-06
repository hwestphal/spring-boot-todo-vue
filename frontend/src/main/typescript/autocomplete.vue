<template>
    <input type="text" :value="value" @input="update" @keyup.enter="enter">
</template>

<script lang="ts" src="./autocomplete.ts"></script>

<style src="awesomplete/awesomplete.css"></style>
