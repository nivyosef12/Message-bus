# Message-bus

 Asynchronous message-bus design pattern, 
    used by micro-services (Star Wars charecters - Han-Solo, Leia, etc...) to subscribes to events(attack events and more) and to simulate the action derived from the signal (message) given to them while taking necessary resources in order to complete the action (simulated by sleeping).
    
 input format -> {
                     "attacks": [
                                    {"serials":[int array], "duration": (int)}
                                                   .
                                                   .
                                                   .
                                ]
                     "R2D2":(int), 
                     "Lando":(int),
                     "Ewoks":(int),
                     "testId":(int),
                     "numOfAttacks":(int)
                 }
                 
***The numbers in the array range from 1 to (num of ewoks), and simulate the resources needed to carry out the attack***
