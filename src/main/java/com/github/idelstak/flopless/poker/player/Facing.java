package com.github.idelstak.flopless.poker.player;

public sealed interface Facing {

    record Open() implements Facing {
    }

    sealed interface Raised extends Facing {

        Position villain();

        record VsUtg() implements Raised {

            @Override
            public Position villain() {
                return new Position.Utg();
            }
        }

        record VsUtg1() implements Raised {

            @Override
            public Position villain() {
                return new Position.Utg1();
            }
        }

        record VsUtg2() implements Raised {

            @Override
            public Position villain() {
                return new Position.Utg2();
            }
        }

        record VsLj() implements Raised {

            @Override
            public Position villain() {
                return new Position.Lj();
            }
        }

        record VsHj() implements Raised {

            @Override
            public Position villain() {
                return new Position.Hj();
            }
        }

        record VsCo() implements Raised {

            @Override
            public Position villain() {
                return new Position.Co();
            }
        }

        record VsBtn() implements Raised {

            @Override
            public Position villain() {
                return new Position.Btn();
            }
        }

        record VsSb() implements Raised {

            @Override
            public Position villain() {
                return new Position.Sb();
            }
        }
    }

    sealed interface ReRaised extends Facing {

        String label();

        record Vs3Bet() implements ReRaised {

            @Override
            public String label() {
                return "3bet";
            }
        }

        record Vs4Bet() implements ReRaised {

            @Override
            public String label() {
                return "4bet";
            }
        }

        record Vs5Bet() implements ReRaised {

            @Override
            public String label() {
                return "5bet";
            }
        }

        record VsAllIn() implements ReRaised {

            @Override
            public String label() {
                return "allin";
            }
        }
    }
}
