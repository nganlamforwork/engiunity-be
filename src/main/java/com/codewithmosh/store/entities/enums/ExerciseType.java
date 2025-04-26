package com.codewithmosh.store.entities.enums;

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
// VOCABULARY("Vocabulary"),
// IDIOMS("Idioms"),
// GRAMMAR("Grammar"),
// COMPREHENSION("Comprehension"),
// TRANSLATION("Translation"),
// PHRASAL_VERBS("Phrasal Verbs"),
// SPELLING("Spelling");

 private final String exerciseType;

 ExerciseType(String exerciseType) {
  this.exerciseType = exerciseType;
 }

 public String getExerciseType() {
  return exerciseType;
 }

 /**
  * Converts a string to the corresponding ExerciseType enum.
  * @param exerciseType the string representation of the exercise type.
  * @return the corresponding ExerciseType enum.
  * @throws IllegalArgumentException if no enum constant matches the given string.
  */
 public static ExerciseType fromString(String exerciseType) {
  for (ExerciseType type : ExerciseType.values()) {
   if (type.getExerciseType().equalsIgnoreCase(exerciseType)) {
    return type;
   }
  }
  throw new IllegalArgumentException("No enum constant with type " + exerciseType);
 }
}
