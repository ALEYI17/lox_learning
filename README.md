# Lox_Learning

Lox_Learning is a project inspired by the book *Crafting Interpreters* by Robert Nystrom. This project focuses on creating an interpreter for the Lox programming language, a language designed to showcase both object-oriented and functional programming paradigms. The interpreter is implemented in Java.

## Features

- **Object-Oriented Programming (OOP):**
  - Classes and instances.
  - Inheritance for code reuse and extensibility.
  - Encapsulation and method invocation.

- **Functional Programming (FP):**
  - First-class functions.
  - Lexical scoping.
  - Support for higher-order functions.

- **Interpreter Design:**
  - Lexical analysis (tokenization).
  - Syntax parsing (AST generation).
  - Semantic analysis and runtime evaluation.

## Usage

You can use the Lox interpreter in two ways:

1. **Interactive Mode (REPL):**
   Run the interpreter without arguments to enter the interactive shell:
   ```bash
   java -cp bin com.lox.Lox
   ```
   You can then type Lox code directly and see the results.

2. **Script Mode:**
   Provide a `.lox` file as an argument to execute a script:
   ```bash
   java -cp bin com.example.lox path/to/script.lox
   ```

## Example

### Lox Code
```lox
class Greeter {
  greet(name) {
    print "Hello, " + name + "!";
  }
}

var greeter = Greeter();
greeter.greet("World");
```

### Output
```
Hello, World!
```

## Resources

- [Crafting Interpreters Book](https://craftinginterpreters.com/)

## Acknowledgments

- Robert Nystrom for the *Crafting Interpreters* book, which inspired this project.

