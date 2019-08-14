package com.github.thatannoyingcatat4am.model

trait Convert[A, B] {
  def convert(a: A): B
}
