/**
 * Created by kaustubh on 4/17/16.
 */
(function () {
    'use strict';

    describe("app test", function () {
        beforeEach(function () {
            module('learning');
        });

        describe("app test", function () {
            it('should have a dummy test', function () {
                expect(1+1).toBe(2);
            });

            it('should have a failed dummy test', function () {
                expect(1+1).toBeGreaterThan(1);
            });
        });
    });
})();
