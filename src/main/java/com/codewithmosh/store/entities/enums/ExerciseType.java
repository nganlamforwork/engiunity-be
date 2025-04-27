package com.codewithmosh.store.entities.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Author: lamlevungan
 * Date: 27/04/2025
 *
 * Enum representing the types of exercises.
 */
public enum ExerciseType {

 WRITING("Writing"),
 READING("Reading"),
 SPEAKING("Speaking"),
 LISTENING("Listening");

 private final String exerciseType;

 ExerciseType(String exerciseType) {
  this.exerciseType = exerciseType;
 }

 @JsonValue
 public String getExerciseType() {
  return exerciseType;
 }

 @JsonCreator
 public static ExerciseType fromString(String exerciseType) {
  for (ExerciseType type : ExerciseType.values()) {
   if (type.getExerciseType().equalsIgnoreCase(exerciseType)) {
    return type;
   }
  }
  throw new IllegalArgumentException("No enum constant with type " + exerciseType);
 }
}