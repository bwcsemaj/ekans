package com.backwardscollection.ekans.config;

public enum Direction {
    UP(){
        @Override
        public Direction getOpposite() {
            return DOWN;
        }
    }, DOWN {
        @Override
        public Direction getOpposite() {
            return UP;
        }
    }, LEFT {
        @Override
        public Direction getOpposite() {
            return RIGHT;
        }
    }, RIGHT {
        @Override
        public Direction getOpposite() {
            return LEFT;
        }
    };
    
    public abstract Direction getOpposite();
}
