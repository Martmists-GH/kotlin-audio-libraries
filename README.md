# Kotlin Audio Libraries

I originally made this repo inspired by [Dasp](https://github.com/RustAudio/dasp), but I'm not sure if I like the extreme modularity.
Feature requests are more than welcome.

## Usage

See the [examples](./examples) folder.

## Features

- [x] Sample
  - TypeAlias of Number with commonized methods 
- [x] Frame
  - Generic array of Samples with support for multiple channels
  - Supports common operations like `map`
- [x] Math Extensions
  - [x] Sinc
  - [x] Decibel conversions
  - [ ] ...
- [x] Buffers
  - [x] RingBuffer
  - [x] ShiftBuffer
  - [x] DelayBuffer
  - [ ] ...
- [x] Filters
  - [x] FIR
  - [x] IIR
  - [ ] ...
- [x] Window
  - [x] Rectangular
  - [x] Triangular
  - [x] CosineSum variants (Hann, Hamming, Blackman, etc.)
  - [x] Gaussian
  - [x] Tukey
  - [x] PlanckTaper
  - [x] Lanczos
  - [ ] ...
- [x] Analysis
  - [x] RMS
  - [x] Peak
  - [ ] ...
- [ ] Complex 
  - [x] Complex
  - [ ] Compatibility methods for Number types
- [ ] FFT
  - [x] FFT
  - [ ] Inverse FFT
