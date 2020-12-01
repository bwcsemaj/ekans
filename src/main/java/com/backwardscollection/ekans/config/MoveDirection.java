package com.backwardscollection.ekans.config;

public enum MoveDirection {
    UP(){
        @Override
        public MoveDirection getOpposite() {
            return DOWN;
        }
    }, DOWN {
        @Override
        public MoveDirection getOpposite() {
            return UP;
        }
    }, LEFT {
        @Override
        public MoveDirection getOpposite() {
            return RIGHT;
        }
    }, RIGHT {
        @Override
        public MoveDirection getOpposite() {
            return LEFT;
        }
    };
    
    public abstract MoveDirection getOpposite();
}
